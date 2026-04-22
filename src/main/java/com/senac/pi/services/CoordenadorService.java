package com.senac.pi.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.senac.pi.DTO.CoordenadorDTO;
import com.senac.pi.entities.Coordenador;
import com.senac.pi.repositories.CoordenadorRepository;

@Service
public class CoordenadorService {

    @Autowired
    private CoordenadorRepository repository;

    @Transactional(readOnly = true)
    public List<CoordenadorDTO> findAll() {
        List<Coordenador> list = repository.findAll();
        return list.stream().map(CoordenadorDTO::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CoordenadorDTO findById(Long id) {
        Coordenador entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Coordenador não encontrado com o ID: " + id));
        return new CoordenadorDTO(entity);
    }

    @Transactional
    public CoordenadorDTO insert(Coordenador obj) {
        // Salva a entidade e retorna o DTO
        obj = repository.save(obj);
        return new CoordenadorDTO(obj);
    }

    @Transactional
    public CoordenadorDTO update(Long id, Coordenador obj) {
        try {
            // getReferenceById é mais eficiente que findById para atualizações
            Coordenador entity = repository.getReferenceById(id);
            updateData(entity, obj);
            entity = repository.save(entity);
            return new CoordenadorDTO(entity);
        } catch (jakarta.persistence.EntityNotFoundException e) {
            throw new RuntimeException("Id não encontrado: " + id);
        }
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Id não encontrado: " + id);
        }
        repository.deleteById(id);
    }

    private void updateData(Coordenador entity, Coordenador obj) {
        entity.setName(obj.getName());
        entity.setEmail(obj.getEmail());
        if (obj.getSenhaHash() != null && !obj.getSenhaHash().isBlank()) {
            entity.setSenhaHash(obj.getSenhaHash());
        }
    }
}