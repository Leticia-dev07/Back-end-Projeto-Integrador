package com.senac.pi.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.senac.pi.DTO.AlunoDTO;
import com.senac.pi.DTO.CursoDTO;
import com.senac.pi.entities.Aluno;
import com.senac.pi.entities.Curso;
import com.senac.pi.entities.enums.UserRole;
import com.senac.pi.repositories.AlunoRepository;
import com.senac.pi.repositories.CursoRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class AlunoService {

    private static final Logger log = LoggerFactory.getLogger(AlunoService.class);

    @Autowired
    private AlunoRepository repository;

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<AlunoDTO> findAll() {
        log.info("Buscando todos os alunos cadastrados...");
        List<Aluno> list = repository.findAll();
        log.info("{} alunos encontrados.", list.size());
        return list.stream()
            .map(AlunoDTO::new)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AlunoDTO findById(Long id) {
        log.info("Buscando aluno com ID: {}", id);
        Aluno entity = repository.findById(id)
            .orElseThrow(() -> {
                log.error("Aluno com ID {} não encontrado no banco.", id);
                return new EntityNotFoundException("Aluno não encontrado");
            });
        return new AlunoDTO(entity);
    }

    @Transactional(readOnly = true)
    public List<AlunoDTO> findByCurso(Long cursoId) {
        log.info("Listando alunos matriculados no curso ID: {}", cursoId);
        List<Aluno> list = repository.findByCursoId(cursoId);
        return list.stream()
            .map(AlunoDTO::new)
            .collect(Collectors.toList());
    }

    /**
     * [NOVO] Retorna a lista de cursos vinculados a um aluno específico.
     */
    @Transactional(readOnly = true)
    public List<CursoDTO> findCursosByAlunoId(Long alunoId) {
        log.info("Listando cursos matriculados do aluno ID: {}", alunoId);
        
        if (!repository.existsById(alunoId)) {
            throw new EntityNotFoundException("Aluno não encontrado com ID: " + alunoId);
        }

        return repository.findCursosByAlunoId(alunoId).stream()
                .map(CursoDTO::new)
                .toList(); // Sintaxe nativa e mais limpa no Java 25
    }

    @Transactional
    public AlunoDTO insert(AlunoDTO dto) {
        log.info("Iniciando cadastro de novo aluno sem vínculo imediato: {}", dto.name());
        try {
            validarDuplicidade(dto);

            Aluno entity = new Aluno();
            copyDtoToEntity(dto, entity);

            entity.setRole(UserRole.ALUNO);
            entity.setSenhaHash(passwordEncoder.encode(dto.senha()));
            entity.setHorasAcumuladas(0);

            entity = repository.save(entity);
            log.info("Aluno {} cadastrado com sucesso! ID: {}", entity.getName(), entity.getId());
            return new AlunoDTO(entity);
        } catch (Exception e) {
            log.error("Erro ao inserir aluno: {}", e.getMessage());
            throw e;
        }
    }

    @Transactional
    public AlunoDTO insertComCurso(Long cursoId, AlunoDTO dto) {
        log.info("Processando aluno {} para o curso ID: {}", dto.name(), cursoId);

        Curso curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> {
                    log.error("Falha ao vincular: Curso ID {} não existe.", cursoId);
                    return new EntityNotFoundException("Curso não encontrado");
                });

        Optional<Aluno> alunoExistente = repository.findByEmail(dto.email());

        if (alunoExistente.isPresent()) {
            Aluno entity = alunoExistente.get();
            
            if (!entity.getMatricula().equals(dto.matricula())) {
                throw new RuntimeException("Este e-mail já está em uso com uma matrícula diferente!");
            }

            if (repository.existsByAlunoIdAndCursoId(entity.getId(), cursoId)) {
                throw new RuntimeException("O aluno já está matriculado neste curso!");
            }

            entity.addCurso(curso);
            entity = repository.save(entity);
            log.info("Aluno {} já existia no banco e foi matriculado no novo curso!", entity.getName());
            return new AlunoDTO(entity);
            
        } else {
            if (repository.existsByMatricula(dto.matricula())) {
                throw new RuntimeException("Esta matrícula já está cadastrada para outro aluno!");
            }

            Aluno entity = new Aluno();
            copyDtoToEntity(dto, entity);

            entity.setRole(UserRole.ALUNO);
            entity.setSenhaHash(passwordEncoder.encode(dto.senha()));
            entity.setHorasAcumuladas(0);
            entity.addCurso(curso);

            entity = repository.save(entity);
            log.info("Novo aluno {} cadastrado e matriculado no curso!", entity.getName());
            return new AlunoDTO(entity);
        }
    }

    @Transactional
    public void matricularEmCurso(Long alunoId, Long cursoId) {
        log.info("Tentativa de matrícula: Aluno ID {} no Curso ID {}", alunoId, cursoId);
        
        Aluno aluno = repository.findById(alunoId)
                .orElseThrow(() -> new EntityNotFoundException("Aluno não encontrado"));

        Curso curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new EntityNotFoundException("Curso não encontrado"));

        if (repository.existsByAlunoIdAndCursoId(alunoId, cursoId)) {
            log.warn("Matrícula negada: Aluno ID {} já está no curso {}.", alunoId, cursoId);
            throw new RuntimeException("O aluno já está matriculado neste curso!");
        }

        aluno.addCurso(curso);
        repository.save(aluno);
        log.info("Matrícula realizada com sucesso!");
    }

    @Transactional
    public AlunoDTO update(Long id, AlunoDTO dto) {
        log.info("Atualizando dados do aluno ID: {}", id);
        try {
            Aluno entity = repository.getReferenceById(id);

            if (!entity.getEmail().equals(dto.email()) && repository.existsByEmail(dto.email())) {
                log.warn("Falha na atualização: E-mail {} já está em uso.", dto.email());
                throw new RuntimeException("O novo e-mail já está em uso.");
            }

            copyDtoToEntity(dto, entity);

            if (dto.senha() != null && !dto.senha().isBlank()) {
                entity.setSenhaHash(passwordEncoder.encode(dto.senha()));
            }

            entity = repository.save(entity);
            log.info("Dados do aluno {} atualizados com sucesso.", entity.getName());
            return new AlunoDTO(entity);

        } catch (EntityNotFoundException e) {
            log.error("Erro ao atualizar: ID {} inexistente.", id);
            throw new EntityNotFoundException("Id não encontrado: " + id);
        }
    }

    public void delete(Long id) {
        log.info("Tentando excluir aluno ID: {}", id);
        if (!repository.existsById(id)) {
            log.error("Exclusão abortada: Aluno ID {} não encontrado.", id);
            throw new EntityNotFoundException("Id não encontrado");
        }
        repository.deleteById(id);
        log.info("Aluno ID {} excluído com sucesso.", id);
    }
    
    @Transactional
    public void desvincularDeCurso(Long alunoId, Long cursoId) {
        log.info("Desvinculando Aluno ID {} do Curso ID {}", alunoId, cursoId);
        
        Aluno aluno = repository.findById(alunoId)
                .orElseThrow(() -> new EntityNotFoundException("Aluno não encontrado"));

        Curso curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new EntityNotFoundException("Curso não encontrado"));

        aluno.getCursos().remove(curso);
        repository.save(aluno);
        log.info("Vínculo removido com sucesso.");
    }

    /**
     * [NOVO] Processa o CSV, cria os alunos (ou reutiliza se o e-mail existir)
     * e os vincula ao curso selecionado.
     */
    @Transactional
    public List<Aluno> cadastrarEmMassaCsv(MultipartFile file, Long cursoId) {
        log.info("Iniciando importação de CSV para o curso ID: {}", cursoId);
        if (file.isEmpty()) throw new RuntimeException("O arquivo CSV está vazio.");

        List<Aluno> alunosSalvos = new ArrayList<>();
        
        // Busca o curso apenas uma vez, caso o ID tenha sido passado
        Curso curso = null;
        if (cursoId != null) {
            curso = cursoRepository.findById(cursoId)
                    .orElseThrow(() -> new RuntimeException("Curso não encontrado para vinculo no CSV."));
        }

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            String linha;
            boolean primeiraLinha = true;

            while ((linha = br.readLine()) != null) {
                if (primeiraLinha) {
                    primeiraLinha = false;
                    continue; // Pula o cabeçalho
                }

                // O Excel no Brasil costuma separar por ; então é seguro separar por vírgula ou ponto e vírgula
                String[] dados = linha.split("[,;]");

                // Espera pelo menos: Nome, Email, Matricula, Turma
                if (dados.length >= 4) {
                    String nome = dados[0].trim();
                    String email = dados[1].trim();
                    String matricula = dados[2].trim();
                    String turma = dados[3].trim();

                    // Verifica se o aluno já existe pelo e-mail
                    Optional<Aluno> alunoOpt = repository.findByEmail(email);
                    Aluno aluno;

                    if (alunoOpt.isPresent()) {
                        aluno = alunoOpt.get();
                        // Se o aluno existe, apenas vincula ao curso (se já não estiver vinculado)
                        if (curso != null && !repository.existsByAlunoIdAndCursoId(aluno.getId(), cursoId)) {
                            aluno.addCurso(curso);
                            aluno = repository.save(aluno);
                        }
                    } else {
                        // Se não existe, cria do zero
                        aluno = new Aluno();
                        aluno.setName(nome);
                        aluno.setEmail(email);
                        aluno.setMatricula(matricula);
                        aluno.setTurma(turma);
                        aluno.setSenhaHash(passwordEncoder.encode("123456")); // Senha padrão
                        aluno.setRole(UserRole.ALUNO);
                        aluno.setHorasAcumuladas(0);
                        
                        if (curso != null) {
                            aluno.addCurso(curso);
                        }
                        aluno = repository.save(aluno);
                    }
                    
                    alunosSalvos.add(aluno);
                }
            }
            log.info("Importação de CSV concluída com sucesso! {} alunos processados.", alunosSalvos.size());
        } catch (Exception e) {
            log.error("Erro ao ler arquivo CSV: {}", e.getMessage());
            throw new RuntimeException("Erro ao processar o CSV: " + e.getMessage());
        }
        
        return alunosSalvos;
    }

    private void validarDuplicidade(AlunoDTO dto) {
        if (repository.existsByEmail(dto.email())) {
            log.warn("Validação falhou: E-mail {} já existe.", dto.email());
            throw new RuntimeException("E-mail já cadastrado!");
        }

        if (repository.existsByMatricula(dto.matricula())) {
            log.warn("Validação falhou: Matrícula {} já existe.", dto.matricula());
            throw new RuntimeException("Matrícula já cadastrada!");
        }
    }

    private void copyDtoToEntity(AlunoDTO dto, Aluno entity) {
        entity.setName(dto.name());
        entity.setEmail(dto.email());
        entity.setMatricula(dto.matricula());
        entity.setTurma(dto.turma());

        if (entity.getHorasAcumuladas() == null) {
            entity.setHorasAcumuladas(0);
        }
    }
}