package com.senac.pi.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.senac.pi.DTO.AlunoDTO;
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

        // NOVO: Verifica se o aluno já existe pelo e-mail
        Optional<Aluno> alunoExistente = repository.findByEmail(dto.email());

        if (alunoExistente.isPresent()) {
            Aluno entity = alunoExistente.get();
            
            // Trava de segurança: se o e-mail for o mesmo, a matrícula também deve bater
            if (!entity.getMatricula().equals(dto.matricula())) {
                throw new RuntimeException("Este e-mail já está em uso com uma matrícula diferente!");
            }

            // Verifica se já está matriculado neste curso específico
            if (repository.existsByAlunoIdAndCursoId(entity.getId(), cursoId)) {
                throw new RuntimeException("O aluno já está matriculado neste curso!");
            }

            // Apenas adiciona o novo vínculo e salva (não reseta a senha nem cria novo usuário)
            entity.addCurso(curso);
            entity = repository.save(entity);
            log.info("Aluno {} já existia no banco e foi matriculado no novo curso!", entity.getName());
            return new AlunoDTO(entity);
            
        } else {
            // Se o e-mail não existe, valida se a matrícula já foi usada por outro e-mail
            if (repository.existsByMatricula(dto.matricula())) {
                throw new RuntimeException("Esta matrícula já está cadastrada para outro aluno!");
            }

            // Cria o aluno do zero e vincula
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