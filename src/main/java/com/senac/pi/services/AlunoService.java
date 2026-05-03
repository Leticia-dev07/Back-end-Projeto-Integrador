package com.senac.pi.services;

import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired
    private AlunoRepository repository;

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<AlunoDTO> findAll() {
        List<Aluno> list = repository.findAll();
        return list.stream()
                .map(AlunoDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AlunoDTO findById(Long id) {
        Aluno entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Aluno não encontrado"));
        return new AlunoDTO(entity);
    }

    @Transactional(readOnly = true)
    public List<AlunoDTO> findByCurso(Long cursoId) {
        List<Aluno> list = repository.findByCursoId(cursoId);
        return list.stream()
                .map(AlunoDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public AlunoDTO insert(AlunoDTO dto) {
        validarDuplicidade(dto);

        Aluno entity = new Aluno();
        copyDtoToEntity(dto, entity);

        entity.setRole(UserRole.ALUNO);
        entity.setSenhaHash(passwordEncoder.encode(dto.senha()));
        entity.setHorasAcumuladas(0);

        entity = repository.save(entity);
        return new AlunoDTO(entity);
    }

    @Transactional
    public AlunoDTO insertComCurso(Long cursoId, AlunoDTO dto) {
        validarDuplicidade(dto);

        Aluno entity = new Aluno();
        copyDtoToEntity(dto, entity);

        Curso curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new EntityNotFoundException("Curso não encontrado"));

        entity.setRole(UserRole.ALUNO);
        entity.setSenhaHash(passwordEncoder.encode(dto.senha()));
        entity.setHorasAcumuladas(0);

        entity.addCurso(curso);
        entity = repository.save(entity);

        return new AlunoDTO(entity);
    }

    @Transactional
    public void matricularEmCurso(Long alunoId, Long cursoId) {
        Aluno aluno = repository.findById(alunoId)
                .orElseThrow(() -> new EntityNotFoundException("Aluno não encontrado"));

        Curso curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new EntityNotFoundException("Curso não encontrado"));

        if (repository.existsByAlunoIdAndCursoId(alunoId, cursoId)) {
            throw new RuntimeException("O aluno já está matriculado neste curso!");
        }

        aluno.addCurso(curso);
        repository.save(aluno);
    }

    @Transactional
    public AlunoDTO update(Long id, AlunoDTO dto) {
        try {
            Aluno entity = repository.getReferenceById(id);

            if (!entity.getEmail().equals(dto.email()) && repository.existsByEmail(dto.email())) {
                throw new RuntimeException("O novo e-mail já está em uso.");
            }

            copyDtoToEntity(dto, entity);

            if (dto.senha() != null && !dto.senha().isBlank()) {
                entity.setSenhaHash(passwordEncoder.encode(dto.senha()));
            }

            entity = repository.save(entity);
            return new AlunoDTO(entity);

        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException("Id não encontrado: " + id);
        }
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Id não encontrado");
        }
        repository.deleteById(id);
    }
    
    @Transactional
    public void desvincularDeCurso(Long alunoId, Long cursoId) {
        // Busca o aluno ou lança exceção
        Aluno aluno = repository.findById(alunoId)
                .orElseThrow(() -> new EntityNotFoundException("Aluno não encontrado"));

        // Busca o curso ou lança exceção
        Curso curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new EntityNotFoundException("Curso não encontrado"));

        // Remove o curso da coleção do aluno (o método removeCurso deve estar na sua entidade Aluno)
        aluno.getCursos().remove(curso);
        
        // Salva a alteração
        repository.save(aluno);
    }

    private void validarDuplicidade(AlunoDTO dto) {
        if (repository.existsByEmail(dto.email())) {
            throw new RuntimeException("E-mail já cadastrado!");
        }

        if (repository.existsByMatricula(dto.matricula())) {
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