package com.senac.pi.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.senac.pi.DTO.SuperAdminDTO;
import com.senac.pi.entities.SuperAdmin;
import com.senac.pi.entities.enums.UserRole;
import com.senac.pi.repositories.SuperAdminRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class SuperAdminService {

    @Autowired
    private SuperAdminRepository repository;

    @Transactional(readOnly = true)
    public List<SuperAdminDTO> findAll() {
        List<SuperAdmin> list = repository.findAll();
        return list.stream().map(SuperAdminDTO::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SuperAdminDTO findById(Long id) {
        SuperAdmin entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Admin não encontrado: " + id));
        return new SuperAdminDTO(entity);
    }

    @Transactional
    public SuperAdminDTO insert(SuperAdmin obj) {
        obj.setRole(UserRole.ADMIN); 
        obj = repository.save(obj);
        return new SuperAdminDTO(obj);
    }

    @Transactional
    public SuperAdminDTO update(Long id, SuperAdmin obj) {
        try {
            SuperAdmin entity = repository.getReferenceById(id);
            updateData(entity, obj);
            entity = repository.save(entity);
            return new SuperAdminDTO(entity);
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException("Id não encontrado: " + id);
        }
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Id não encontrado: " + id);
        }
        repository.deleteById(id);
    }

    private void updateData(SuperAdmin entity, SuperAdmin obj) {
        entity.setName(obj.getName());
        entity.setEmail(obj.getEmail());
        if (obj.getSenhaHash() != null && !obj.getSenhaHash().isBlank()) {
            entity.setSenhaHash(obj.getSenhaHash());
        }
    }
}