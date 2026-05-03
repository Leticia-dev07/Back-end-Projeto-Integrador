package com.senac.pi.services;

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
import com.senac.pi.entities.Certificado;
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
    private FileService fileService;

    @Transactional(readOnly = true)
    public List<SubmissaoDTO> findAll() {
        log.info("### SUBMISSÃO ### Listando todas as submissões.");
        return repository.findAll().stream().map(SubmissaoDTO::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SubmissaoDTO findById(Long id) {
        log.info("### SUBMISSÃO ### Buscando submissão ID: {}", id);
        Submissao entity = repository.findById(id)
                .orElseThrow(() -> {
                    log.error("### SUBMISSÃO ### ID {} não encontrado.", id);
                    return new EntityNotFoundException("Submissão não encontrada");
                });
        return new SubmissaoDTO(entity);
    }

    @Transactional
    public SubmissaoDTO insert(Submissao entity, MultipartFile arquivo) {
        log.info("### SUBMISSÃO ### Iniciando novo processo de submissão...");

        if (arquivo == null || arquivo.isEmpty()) {
            log.warn("### SUBMISSÃO ### Abortado: Arquivo ausente.");
            throw new RuntimeException("O envio do arquivo de certificado é obrigatório.");
        }

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

        // LÓGICA DO CERTIFICADO
        String nomeArquivoNoDisco = fileService.saveFile(arquivo);

        Certificado certificado = new Certificado();
        certificado.setNomeArquivo(arquivo.getOriginalFilename());
        certificado.setUrlArquivo(nomeArquivoNoDisco);
        
        entity.setCertificado(certificado);
        certificado.setSubmissao(entity);

        // FINALIZAÇÃO
        entity.setAluno(aluno);
        entity.setCategoria(categoria);
        entity.setDataEnvio(Instant.now());
        entity.setStatus(StatusSubmissao.PENDENTE);
        entity.setHorasAproveitadas(categoria.getHorasPorCertificado());

        entity = repository.save(entity);
        log.info("### SUBMISSÃO ### Sucesso! Submissão ID {} criada e aguardando aprovação.", entity.getId());
        return new SubmissaoDTO(entity);
    }

    @Transactional
    public SubmissaoDTO aprovar(Long id) {
        log.info("### FLUXO ### Aprovando submissão ID: {}", id);
        Submissao submissao = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Submissão não encontrada"));

        if (submissao.getStatus() != StatusSubmissao.PENDENTE) {
            log.warn("### FLUXO ### Tentativa de aprovar submissão já processada (Status: {}).", submissao.getStatus());
            throw new RuntimeException("Esta submissão já foi processada.");
        }

        submissao.setStatus(StatusSubmissao.APROVADO);
        
        Aluno aluno = submissao.getAluno();
        int horasAnteriores = (aluno.getHorasAcumuladas() != null) ? aluno.getHorasAcumuladas() : 0;
        int novasHoras = submissao.getHorasAproveitadas();
        
        aluno.setHorasAcumuladas(horasAnteriores + novasHoras);
        log.info("### FLUXO ### Horas do Aluno {}: {}h -> {}h", aluno.getName(), horasAnteriores, aluno.getHorasAcumuladas());
        
        alunoRepository.save(aluno);
        submissao = repository.save(submissao);

        enviarEmailSilencioso(aluno.getEmail(), "Certificado Aprovado ✅", 
                "Olá, " + aluno.getName() + "!\nSeu certificado foi APROVADO.\nHoras computadas: " + novasHoras + "h");
        
        return new SubmissaoDTO(submissao);
    }

    @Transactional
    public SubmissaoDTO rejeitar(Long id, String observacao) {
        log.info("### FLUXO ### Rejeitando submissão ID: {}. Motivo: {}", id, observacao);
        Submissao submissao = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Submissão não encontrada"));
        
        submissao.setStatus(StatusSubmissao.REJEITADO);
        submissao.setObservacaoCoordenador(observacao);
        submissao = repository.save(submissao);

        Aluno aluno = submissao.getAluno();
        enviarEmailSilencioso(aluno.getEmail(), "Certificado Reprovado ❌", 
                "Olá, " + aluno.getName() + "!\nSeu certificado foi REPROVADO.\nMotivo: " + observacao);
        
        return new SubmissaoDTO(submissao);
    }

    private void enviarEmailSilencioso(String para, String assunto, String corpo) {
        try {
            emailService.enviarEmail(para, assunto, corpo);
            log.info("### EMAIL ### Notificação enviada para: {}", para);
        } catch (Exception e) {
            log.error("### EMAIL ### Falha ao enviar e-mail para {}: {}", para, e.getMessage());
        }
    }
}