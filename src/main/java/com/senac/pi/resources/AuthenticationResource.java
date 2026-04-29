package com.senac.pi.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.senac.pi.DTO.LoginResponseDTO;
import com.senac.pi.entities.User;
import com.senac.pi.services.TokenService;

@RestController
@RequestMapping("auth")
@CrossOrigin(origins = "http://127.0.0.1:5500") // Permite a conexão do seu Live Server
public class AuthenticationResource {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    /**
     * Realiza a autenticação e retorna o Token JWT junto com a Role do usuário.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody AuthenticationDTO data) {
        // Cria o token de credenciais para o Spring Security validar
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());
        
        // Valida e-mail e senha (usando o BCrypt configurado)
        var auth = this.authenticationManager.authenticate(usernamePassword);

        // Se a validação passar, pegamos o usuário completo para acessar a Role
        var user = (User) auth.getPrincipal();
        
        // Gera o Token JWT baseado no usuário
        var token = tokenService.generateToken(user);

        // Retorna a resposta completa: Token + Nome da Role (ADMIN, COORDENADOR ou ALUNO)
        return ResponseEntity.ok(new LoginResponseDTO(token, user.getRole().name()));
    }

    /**
     * DTO interno para capturar os dados do JSON enviado pelo Frontend.
     */
    public record AuthenticationDTO(String email, String password) {}
}