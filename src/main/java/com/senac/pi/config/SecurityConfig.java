package com.senac.pi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Desabilita CSRF para conseguir testar POST/PUT sem erro
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll() // Libera QUALQUER rota sem senha
            )
            .headers(headers -> headers.frameOptions(frame -> frame.disable())); // Libera o console do H2 se estiver usando
        
        return http.build();
    }
}