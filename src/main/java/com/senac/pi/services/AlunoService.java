package com.senac.pi.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.senac.pi.DTO.AlunoDTO;
import com.senac.pi.entities.Aluno;
import com.senac.pi.repositories.AlunoRepository;

@Service
public class AlunoService {

    @Autowired
    private AlunoRepository repository;

    @Transactional(readOnly = true)
    public List<AlunoDTO> findAll() {
        List<Aluno> list = repository.findAll();
        // Converte a lista de Aluno para AlunoDTO
        return list.stream().map(x -> new AlunoDTO(x.getId(), x.getName(), x.getEmail(), x.getMatricula(), x.getTurma(), null))
                   .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AlunoDTO findById(Long id) {
        Optional<Aluno> obj = repository.findById(id);
        Aluno entity = obj.orElseThrow(() -> new RuntimeException("Aluno não encontrado"));
        return new AlunoDTO(entity.getId(), entity.getName(), entity.getEmail(), entity.getMatricula(), entity.getTurma(), null);
    }

    @Transactional
    public AlunoDTO insert(AlunoDTO dto) {
        Aluno entity = new Aluno();
        copyDtoToEntity(dto, entity);
        entity.setSenhaHash(dto.senhaHash()); // No futuro: criptografar aqui
        entity = repository.save(entity);
        return new AlunoDTO(entity.getId(), entity.getName(), entity.getEmail(), entity.getMatricula(), entity.getTurma(), null);
    }

    @Transactional
    public AlunoDTO update(Long id, AlunoDTO dto) {
        try {
            Aluno entity = repository.getReferenceById(id);
            copyDtoToEntity(dto, entity);
            entity = repository.save(entity);
            return new AlunoDTO(entity.getId(), entity.getName(), entity.getEmail(), entity.getMatricula(), entity.getTurma(), null);
        } catch (Exception e) {
            throw new RuntimeException("Id não encontrado: " + id);
        }
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Id não encontrado");
        }
        repository.deleteById(id);
    }

    private void copyDtoToEntity(AlunoDTO dto, Aluno entity) {
        entity.setName(dto.name());
        entity.setEmail(dto.email());
        entity.setMatricula(dto.matricula());
        entity.setTurma(dto.turma());
    }
}