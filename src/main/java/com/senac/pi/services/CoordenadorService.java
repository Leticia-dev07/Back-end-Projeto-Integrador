package com.senac.pi.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.senac.pi.DTO.CoordenadorDTO;
import com.senac.pi.entities.Coordenador;
import com.senac.pi.entities.Curso;
import com.senac.pi.entities.enums.UserRole;
import com.senac.pi.repositories.CoordenadorRepository;
import com.senac.pi.repositories.CursoRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class CoordenadorService {

    @Autowired
    private CoordenadorRepository repository;

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<CoordenadorDTO> findAll() {
        List<Coordenador> list = repository.findAll();
        return list.stream()
                .map(CoordenadorDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CoordenadorDTO findById(Long id) {
        Coordenador entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Coordenador não encontrado: " + id));
        return new CoordenadorDTO(entity);
    }

    @Transactional
    public CoordenadorDTO insert(CoordenadorDTO dto) {
        Coordenador entity = new Coordenador();
        entity.setName(dto.name());
        entity.setEmail(dto.email());
        entity.setRole(UserRole.COORDENADOR);

        if (dto.password() != null && !dto.password().isBlank()) {
            entity.setSenhaHash(passwordEncoder.encode(dto.password()));
        }

        entity = repository.save(entity);
        return new CoordenadorDTO(entity);
    }

    @Transactional
    public void vincularCurso(Long coordId, Long cursoId) {
        Coordenador coord = repository.findById(coordId)
                .orElseThrow(() -> new EntityNotFoundException("Coordenador não encontrado"));

        Curso curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new EntityNotFoundException("Curso não encontrado"));

        // Adiciona a relação em ambos os lados para manter o estado do objeto sincronizado
        curso.getCoordenadores().add(coord);
        coord.getCursos().add(curso);

        cursoRepository.save(curso);
    }

    /**
     * Remove o vínculo entre um coordenador e um curso na tabela intermediária.
     */
    @Transactional
    public void desvincularCurso(Long coordId, Long cursoId) {
        Coordenador coord = repository.findById(coordId)
                .orElseThrow(() -> new EntityNotFoundException("Coordenador não encontrado"));

        Curso curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new EntityNotFoundException("Curso não encontrado"));

        // Remove a relação de ambos os lados
        curso.getCoordenadores().remove(coord);
        coord.getCursos().remove(curso);

        // Salva a alteração (o JPA se encarrega de deletar a linha na tabela de junção)
        cursoRepository.save(curso);
    }

    @Transactional
    public CoordenadorDTO update(Long id, CoordenadorDTO dto) {
        try {
            Coordenador entity = repository.getReferenceById(id);
            entity.setName(dto.name());
            entity.setEmail(dto.email());

            if (dto.password() != null && !dto.password().isBlank()) {
                entity.setSenhaHash(passwordEncoder.encode(dto.password()));
            }

            entity = repository.save(entity);
            return new CoordenadorDTO(entity);
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException("Id não encontrado: " + id);
        }
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Id não encontrado: " + id);
        }
        repository.deleteById(id);
    }
}