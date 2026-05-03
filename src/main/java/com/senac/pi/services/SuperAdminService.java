package com.senac.pi.services;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    // Logger manual para rastrear ações de alto nível
    private static final Logger log = LoggerFactory.getLogger(SuperAdminService.class);

    @Autowired
    private SuperAdminRepository repository;

    @Transactional(readOnly = true)
    public List<SuperAdminDTO> findAll() {
        log.info("### ADMIN ### Listando todos os administradores do sistema.");
        List<SuperAdmin> list = repository.findAll();
        log.info("### ADMIN ### Total de administradores encontrados: {}", list.size());
        return list.stream().map(SuperAdminDTO::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SuperAdminDTO findById(Long id) {
        log.info("### ADMIN ### Buscando administrador ID: {}", id);
        SuperAdmin entity = repository.findById(id)
                .orElseThrow(() -> {
                    log.error("### ADMIN ### Falha: Administrador ID {} não encontrado.", id);
                    return new EntityNotFoundException("Admin não encontrado: " + id);
                });
        return new SuperAdminDTO(entity);
    }

    @Transactional
    public SuperAdminDTO insert(SuperAdmin obj) {
        log.info("### ADMIN ### Criando novo SuperAdmin: {}", obj.getName());
        try {
            obj.setRole(UserRole.ADMIN); 
            obj = repository.save(obj);
            log.info("### ADMIN ### Novo administrador salvo com sucesso! ID: {}", obj.getId());
            return new SuperAdminDTO(obj);
        } catch (Exception e) {
            log.error("### ADMIN ### Erro ao inserir novo administrador: {}", e.getMessage());
            throw e;
        }
    }

    @Transactional
    public SuperAdminDTO update(Long id, SuperAdmin obj) {
        log.info("### ADMIN ### Atualizando dados do administrador ID: {}", id);
        try {
            SuperAdmin entity = repository.getReferenceById(id);
            updateData(entity, obj);
            entity = repository.save(entity);
            log.info("### ADMIN ### Dados do administrador ID {} atualizados.", id);
            return new SuperAdminDTO(entity);
        } catch (EntityNotFoundException e) {
            log.error("### ADMIN ### Erro no update: Administrador ID {} não localizado.", id);
            throw new EntityNotFoundException("Id não encontrado: " + id);
        }
    }

    @Transactional
    public void delete(Long id) {
        log.info("### ADMIN ### Solicitada exclusão permanente do administrador ID: {}", id);
        if (!repository.existsById(id)) {
            log.warn("### ADMIN ### Tentativa de deleção falhou: ID {} inexistente.", id);
            throw new EntityNotFoundException("Id não encontrado: " + id);
        }
        repository.deleteById(id);
        log.info("### ADMIN ### Administrador ID {} removido com sucesso.", id);
    }

    private void updateData(SuperAdmin entity, SuperAdmin obj) {
        entity.setName(obj.getName());
        entity.setEmail(obj.getEmail());
        if (obj.getSenhaHash() != null && !obj.getSenhaHash().isBlank()) {
            log.info("### ADMIN ### Senha do administrador sendo alterada.");
            entity.setSenhaHash(obj.getSenhaHash());
        }
    }
}