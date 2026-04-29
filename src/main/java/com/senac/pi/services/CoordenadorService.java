package com.senac.pi.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
<<<<<<< HEAD
import org.springframework.security.crypto.password.PasswordEncoder;
=======
>>>>>>> 605a1f1f0e30830dd253152ec3f1ec4a130018bc
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

<<<<<<< HEAD
    @Autowired
    private PasswordEncoder passwordEncoder; // Injetado para o BCrypt

    @Transactional(readOnly = true)
    public List<CoordenadorDTO> findAll() {
        List<Coordenador> list = repository.findAll();
        return list.stream()
                   .map(CoordenadorDTO::new)
                   .collect(Collectors.toList());
=======
    @Transactional(readOnly = true)
    public List<CoordenadorDTO> findAll() {
        List<Coordenador> list = repository.findAll();
        return list.stream().map(CoordenadorDTO::new).collect(Collectors.toList());
>>>>>>> 605a1f1f0e30830dd253152ec3f1ec4a130018bc
    }

    @Transactional(readOnly = true)
    public CoordenadorDTO findById(Long id) {
        Coordenador entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Coordenador não encontrado: " + id));
        return new CoordenadorDTO(entity);
    }

    @Transactional
    public CoordenadorDTO insert(Coordenador obj) {
<<<<<<< HEAD
        // Garante que o perfil seja COORDENADOR
        obj.setRole(UserRole.COORDENADOR);
        
        // Aplica o BCrypt na senha recebida
        if (obj.getSenhaHash() != null && !obj.getSenhaHash().isBlank()) {
            String encryptedPassword = passwordEncoder.encode(obj.getSenhaHash());
            obj.setSenhaHash(encryptedPassword);
        }
        
=======
        obj.setRole(UserRole.COORDENADOR); // Garante a role correta
>>>>>>> 605a1f1f0e30830dd253152ec3f1ec4a130018bc
        obj = repository.save(obj);
        return new CoordenadorDTO(obj);
    }

    /**
<<<<<<< HEAD
     * Associa um coordenador a um curso.
=======
     * REGRA: Associar o coordenador a um curso.
     * Como é ManyToMany, um coordenador pode ser chamado várias vezes para cursos diferentes.
>>>>>>> 605a1f1f0e30830dd253152ec3f1ec4a130018bc
     */
    @Transactional
    public void vincularCurso(Long coordId, Long cursoId) {
        Coordenador coord = repository.findById(coordId)
                .orElseThrow(() -> new EntityNotFoundException("Coordenador não encontrado"));
        Curso curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new EntityNotFoundException("Curso não encontrado"));

<<<<<<< HEAD
        // Adiciona o coordenador à lista de coordenadores do curso
=======
        // Adiciona o coordenador ao curso (dono da relação no seu mapeamento)
>>>>>>> 605a1f1f0e30830dd253152ec3f1ec4a130018bc
        curso.getCoordenadores().add(coord);
        cursoRepository.save(curso);
    }

    @Transactional
    public CoordenadorDTO update(Long id, Coordenador obj) {
        try {
            Coordenador entity = repository.getReferenceById(id);
            updateData(entity, obj);
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

    private void updateData(Coordenador entity, Coordenador obj) {
        entity.setName(obj.getName());
        entity.setEmail(obj.getEmail());
<<<<<<< HEAD
        
        // Se uma nova senha for enviada no update, criptografamos também
        if (obj.getSenhaHash() != null && !obj.getSenhaHash().isBlank()) {
            entity.setSenhaHash(passwordEncoder.encode(obj.getSenhaHash()));
=======
        if (obj.getSenhaHash() != null && !obj.getSenhaHash().isBlank()) {
            entity.setSenhaHash(obj.getSenhaHash());
>>>>>>> 605a1f1f0e30830dd253152ec3f1ec4a130018bc
        }
    }
}