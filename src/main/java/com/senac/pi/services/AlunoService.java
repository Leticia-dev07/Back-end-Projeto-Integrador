package com.senac.pi.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
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

    @Transactional(readOnly = true)
    public List<AlunoDTO> findAll() {
        List<Aluno> list = repository.findAll();
        return list.stream()
                   .map(AlunoDTO::new) // Usando o construtor que criamos no Record
                   .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AlunoDTO findById(Long id) {
        Aluno entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Aluno não encontrado"));
        return new AlunoDTO(entity);
    }

    @Transactional
    public AlunoDTO insert(AlunoDTO dto) {
        Aluno entity = new Aluno();
        copyDtoToEntity(dto, entity);
        
        // REGRA: Garantir estado inicial
        entity.setRole(UserRole.ALUNO);
        entity.setSenhaHash(dto.senha()); 
        entity.setHorasAcumuladas(0); // Todo aluno começa com 0 horas
        
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

        aluno.getCursos().add(curso);
        repository.save(aluno);
    }

    @Transactional
    public AlunoDTO update(Long id, AlunoDTO dto) {
        try {
            Aluno entity = repository.getReferenceById(id);
            copyDtoToEntity(dto, entity);
            // Nota: Não atualizamos horasAcumuladas aqui para evitar fraude manual via Profile
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

    private void copyDtoToEntity(AlunoDTO dto, Aluno entity) {
        entity.setName(dto.name());
        entity.setEmail(dto.email());
        entity.setMatricula(dto.matricula());
        entity.setTurma(dto.turma());
        // Se o DTO trouxer horas e a entidade estiver nula (novo registro), inicializamos
        if (entity.getHorasAcumuladas() == null) {
            entity.setHorasAcumuladas(0);
        }
    }
}