package com.senac.pi.services;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.senac.pi.entities.User;

@Service
public class TokenService {

    // Logger manual para monitorar a segurança
    private static final Logger log = LoggerFactory.getLogger(TokenService.class);

    @Value("${api.security.token.secret}")
    private String secret;

    public String generateToken(User user) {
        log.info("### AUTH ### Gerando token para o usuário: {}", user.getEmail());
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String token = JWT.create()
                    .withIssuer("pi-senac")
                    .withSubject(user.getEmail())
                    .withClaim("role", user.getRole().name())
                    .withExpiresAt(genExpirationDate())
                    .sign(algorithm);
            
            log.info("### AUTH ### Token gerado com sucesso para a role: {}", user.getRole().name());
            return token;
        } catch (JWTCreationException exception) {
            log.error("### AUTH ### Erro crítico ao criar o token JWT: {}", exception.getMessage());
            throw new RuntimeException("Erro ao gerar token", exception);
        }
    }

    public String validateToken(String token) {
        if (token == null || token.isBlank()) {
            return "";
        }

        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String subject = JWT.require(algorithm)
                    .withIssuer("pi-senac")
                    .build()
                    .verify(token)
                    .getSubject();
            
            log.debug("### AUTH ### Token validado com sucesso para: {}", subject);
            return subject;
        } catch (JWTVerificationException exception) {
            // Logamos como aviso (warn) pois pode ser apenas um token expirado
            log.warn("### AUTH ### Falha na validação do token: {}", exception.getMessage());
            return "";
        }
    }

    private Instant genExpirationDate() {
        // Define expiração para 2 horas no fuso de Brasília (-03:00)
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}