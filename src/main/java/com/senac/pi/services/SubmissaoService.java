package com.senac.pi.services;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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

    /**
     * Retorna a entidade completa (com dadosArquivo) — usada pelo endpoint de download.
     * Separado do findById para não expor os bytes binários no DTO JSON.
     */
    @Transactional(readOnly = true)
    public Submissao findEntityById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> {
                    log.error("### SUBMISSÃO ### ID {} não encontrado.", id);
                    return new EntityNotFoundException("Submissão não encontrada: " + id);
                });
    }

    @Transactional
    public SubmissaoDTO insert(Submissao entity, MultipartFile arquivo) {
        log.info("### SUBMISSÃO ### Iniciando novo processo de submissão no Banco de Dados...");

        if (arquivo == null || arquivo.isEmpty()) {
            log.warn("### SUBMISSÃO ### Abortado: Arquivo ausente.");
            throw new RuntimeException("O envio do arquivo de certificado é obrigatório.");
        }

        try {
            Aluno aluno = alunoRepository.findById(entity.getAluno().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Aluno não encontrado"));

            Categoria categoria = categoriaRepository.findById(entity.getCategoria().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada"));

            log.info("### SUBMISSÃO ### Aluno: {} | Categoria: {}", aluno.getName(), categoria.getArea());

            // Validação de Regra de Negócio (Limite por Semestre)
            Instant dataInicioSemestre = Instant.now().minus(180, ChronoUnit.DAYS);
            long totalEnviado = repository.countByAlunoAndCategoriaInPeriod(
                    aluno.getId(),
                    categoria.getId(),
                    dataInicioSemestre
            );

            if (totalEnviado >= categoria.getLimiteSubmissoesSemestre()) {
                log.warn("### SUBMISSÃO ### Bloqueado: Aluno atingiu o limite da categoria '{}'.", categoria.getArea());
                throw new RuntimeException("Limite de envios atingido para a categoria: " + categoria.getArea());
            }

            entity.setNomeArquivo(arquivo.getOriginalFilename());
            entity.setTipoArquivo(arquivo.getContentType());
            entity.setDadosArquivo(arquivo.getBytes());

            entity.setAluno(aluno);
            entity.setCategoria(categoria);
            entity.setDataEnvio(Instant.now());
            entity.setStatus(StatusSubmissao.PENDENTE);
            entity.setHorasAproveitadas(categoria.getHorasPorCertificado());

            // Primeiro save para gerar o ID
            entity = repository.save(entity);

            // URL aponta para o novo endpoint de download
            String urlDownload = "http://localhost:8080/submissoes/" + entity.getId() + "/arquivo";
            entity.setUrlArquivo(urlDownload);

            // Segundo save para persistir a URL
            entity = repository.save(entity);

            log.info("### SUBMISSÃO ### Sucesso! Arquivo salvo. Submissão ID {}.", entity.getId());
            return new SubmissaoDTO(entity);

        } catch (IOException e) {
            log.error("### SUBMISSÃO ### Erro ao processar bytes do arquivo: {}", e.getMessage());
            throw new RuntimeException("Falha ao ler o arquivo enviado.");
        }
    }

    @Transactional
    public SubmissaoDTO aprovar(Long id) {
        log.info("### FLUXO ### Aprovando submissão ID: {}", id);
        Submissao submissao = findEntityById(id);

        if (submissao.getStatus() != StatusSubmissao.PENDENTE) {
            log.warn("### FLUXO ### Tentativa de aprovar submissão já processada.");
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
        log.info("### FLUXO ### Rejeitando submissão ID: {}", id);
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