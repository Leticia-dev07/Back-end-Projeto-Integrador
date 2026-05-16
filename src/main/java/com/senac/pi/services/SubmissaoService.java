package com.senac.pi.services;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.senac.pi.DTO.SubmissaoDTO;
import com.senac.pi.entities.Aluno;
import com.senac.pi.entities.Categoria;
import com.senac.pi.entities.Submissao;
import com.senac.pi.entities.enums.StatusSubmissao;
import com.senac.pi.repositories.AlunoRepository;
import com.senac.pi.repositories.CategoriaRepository;
import com.senac.pi.repositories.SubmissaoRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class SubmissaoService {

    private static final Logger log = LoggerFactory.getLogger(SubmissaoService.class);

    @Autowired
    private EmailService emailService;

    @Autowired
    private SubmissaoRepository repository;

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private FileService fileService; // Serviço de S3 (Supabase)

    @Transactional(readOnly = true)
    public List<SubmissaoDTO> findAll() {
        log.info("### SUBMISSÃO ### Listando todas as submissões.");
        return repository.findAll().stream().map(SubmissaoDTO::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SubmissaoDTO findById(Long id) {
        log.info("### SUBMISSÃO ### Buscando submissão ID: {}", id);
        return new SubmissaoDTO(findEntityById(id));
    }

    @Transactional(readOnly = true)
    public Submissao findEntityById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> {
                    log.error("### SUBMISSÃO ### ID {} não encontrado.", id);
                    return new EntityNotFoundException("Submissao não encontrada: " + id);
                });
    }

    @Transactional
    public SubmissaoDTO insert(Submissao entity, MultipartFile arquivo) {
        log.info("### SUBMISSÃO ### Iniciando novo processo de submissão via S3...");

        if (arquivo == null || arquivo.isEmpty()) {
            throw new RuntimeException("O envio do arquivo de certificado é obrigatório.");
        }

        try {
            Aluno aluno = alunoRepository.findById(entity.getAluno().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Aluno não encontrado"));

            Categoria categoria = categoriaRepository.findById(entity.getCategoria().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada"));

            // ==========================================
            // VALIDAÇÃO DE REGRA: SEMESTRE DINÂMICO
            // ==========================================
            ZonedDateTime agora = ZonedDateTime.now(ZoneId.of("America/Sao_Paulo"));
            int anoAtual = agora.getYear();
            int mesAtual = agora.getMonthValue();
            
            ZonedDateTime inicioSemestre;

            if (mesAtual <= 6) {
                // 1º Semestre: 1º de Janeiro à meia-noite
                inicioSemestre = ZonedDateTime.of(anoAtual, 1, 1, 0, 0, 0, 0, agora.getZone());
                log.info("### REGRA ### Validando limite para o 1º Semestre de {}", anoAtual);
            } else {
                // 2º Semestre: 1º de Julho à meia-noite
                inicioSemestre = ZonedDateTime.of(anoAtual, 7, 1, 0, 0, 0, 0, agora.getZone());
                log.info("### REGRA ### Validando limite para o 2º Semestre de {}", anoAtual);
            }

            // Conta quantas submissões o aluno fez desde o início do semestre atual
            long totalEnviado = repository.countByAlunoAndCategoriaInPeriod(
                    aluno.getId(),
                    categoria.getId(),
                    inicioSemestre.toInstant()
            );

            if (totalEnviado >= categoria.getLimiteSubmissoesSemestre()) {
                throw new RuntimeException("Limite de envios atingido para a categoria: " + categoria.getArea());
            }
            // ==========================================

            // --- FLUXO DE ARQUIVO (S3 SUPABASE) ---
            String urlNuvem = fileService.saveFile(arquivo);

            entity.setNomeArquivo(arquivo.getOriginalFilename());
            entity.setTipoArquivo(arquivo.getContentType());
            entity.setUrlArquivo(urlNuvem); // URL do Supabase salva no banco
            
            entity.setAluno(aluno);
            entity.setCategoria(categoria);
            entity.setDataEnvio(Instant.now());
            entity.setStatus(StatusSubmissao.PENDENTE);
            entity.setHorasAproveitadas(categoria.getHorasPorCertificado());

            entity = repository.save(entity);

            log.info("### SUBMISSÃO ### Sucesso! Submissão ID {} salva. URL: {}", entity.getId(), urlNuvem);
            return new SubmissaoDTO(entity);

        } catch (Exception e) {
            log.error("### SUBMISSÃO ### Erro no processo de inserção: {}", e.getMessage());
            throw new RuntimeException("Falha ao processar submissão: " + e.getMessage());
        }
    }

    @Transactional
    public SubmissaoDTO aprovar(Long id) {
        Submissao submissao = findEntityById(id);

        if (submissao.getStatus() != StatusSubmissao.PENDENTE) {
            throw new RuntimeException("Esta submissão já foi processada.");
        }

        submissao.setStatus(StatusSubmissao.APROVADO);

        Aluno aluno = submissao.getAluno();
        int horasAnteriores = (aluno.getHorasAcumuladas() != null) ? aluno.getHorasAcumuladas() : 0;
        aluno.setHorasAcumuladas(horasAnteriores + submissao.getHorasAproveitadas());

        alunoRepository.save(aluno);
        submissao = repository.save(submissao);

        enviarEmailSilencioso(aluno.getEmail(), "Certificado Aprovado ✅",
                "Olá, " + aluno.getName() + "! Seu certificado foi APROVADO.");

        return new SubmissaoDTO(submissao);
    }

    @Transactional
    public SubmissaoDTO rejeitar(Long id, String observacao) {
        Submissao submissao = findEntityById(id);

        submissao.setStatus(StatusSubmissao.REJEITADO);
        submissao.setObservacaoCoordenador(observacao);
        submissao = repository.save(submissao);

        Aluno aluno = submissao.getAluno();
        enviarEmailSilencioso(aluno.getEmail(), "Certificado Reprovado ❌",
                "Motivo: " + observacao);

        return new SubmissaoDTO(submissao);
    }

    private void enviarEmailSilencioso(String para, String assunto, String corpo) {
        try {
            emailService.enviarEmail(para, assunto, corpo);
        } catch (Exception e) {
            log.error("### EMAIL ### Falha: {}", e.getMessage());
        }
    }
}