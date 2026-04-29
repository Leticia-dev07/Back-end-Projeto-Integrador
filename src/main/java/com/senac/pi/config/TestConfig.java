package com.senac.pi.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.senac.pi.entities.SuperAdmin;
import com.senac.pi.repositories.SuperAdminRepository;
import com.senac.pi.repositories.UserRepository;

@Configuration
public class TestConfig implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SuperAdminRepository superAdminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        
        // Verifica se a tabela de usuários está vazia
        if (userRepository.count() == 0) {
            System.out.println("SISTEMA: Banco vazio. Criando Super Admin inicial...");

            // Definimos a senha: "admin123" (será salva como Hash BCrypt)
            String senhaCriptografada = passwordEncoder.encode("admin123");

            SuperAdmin admin = new SuperAdmin(null, "Administrador Senac", "admin@senac.com", senhaCriptografada);
            
            superAdminRepository.save(admin);
            
            System.out.println("SISTEMA: Super Admin criado com sucesso!");
            System.out.println("Login: admin@senac.com | Senha: admin123");
        }
    }
}