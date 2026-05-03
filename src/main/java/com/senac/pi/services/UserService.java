package com.senac.pi.services;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.senac.pi.DTO.UserDTO;
import com.senac.pi.entities.User;
import com.senac.pi.repositories.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UserService {

    // Logger manual para rastreabilidade de usuários
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository repository;

    @Transactional(readOnly = true)
    public List<UserDTO> findAll() {
        log.info("### USER ### Listando todos os usuários da base (Alunos, Coordenadores e Admins).");
        List<User> list = repository.findAll();
        log.info("### USER ### Total de registros encontrados: {}", list.size());
        return list.stream().map(UserDTO::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserDTO findById(Long id) {
        log.info("### USER ### Buscando usuário genérico ID: {}", id);
        User entity = repository.findById(id)
                .orElseThrow(() -> {
                    log.error("### USER ### Falha: Usuário com ID {} não existe.", id);
                    return new EntityNotFoundException("Usuário não encontrado");
                });
        
        log.info("### USER ### Usuário localizado: {} | Role: {}", entity.getEmail(), entity.getRole());
        return new UserDTO(entity);
    }
}