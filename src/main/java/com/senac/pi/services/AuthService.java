package com.senac.pi.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.senac.pi.repositories.UserRepository;

@Service
public class AuthService implements UserDetailsService {

    // Criado o logger manual para evitar erros de compilação
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("### AUTH ### Tentativa de autenticação para o e-mail: {}", username);
        
        return repository.findByEmail(username)
                .map(user -> {
                    log.info("### AUTH ### Usuário encontrado no banco! Prosseguindo com a validação de senha...");
                    return user;
                })
                .orElseThrow(() -> {
                    log.error("### AUTH ### Falha no login: O e-mail '{}' não existe na base de dados.", username);
                    return new UsernameNotFoundException("Usuário não encontrado");
                });
    }
}