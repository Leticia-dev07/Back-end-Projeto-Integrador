package com.senac.pi.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.senac.pi.repositories.UserRepository;
import com.senac.pi.services.TokenService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // Ignorar rotas publicas sem segurança

        if (isPublicRoute(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {

            // Validação jwt

            String token = recoverToken(request);

            if (token != null) {
                String login = tokenService.validateToken(token);

                if (login != null) {
                    UserDetails user = userRepository.findByEmail(login).orElse(null);

                    if (user != null) {
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(
                                        user,
                                        null,
                                        user.getAuthorities()
                                );

                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            }

        } catch (Exception e) {
            // Evita quebrar requisição com o token
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    // Rotas publicas

    private boolean isPublicRoute(String path) {
        return path.startsWith("/certificados/") ||   
               path.startsWith("/auth/") ||          
               path.startsWith("/v3/api-docs") ||     
               path.startsWith("/swagger-ui") ||
               path.startsWith("/swagger-ui.html");
    }

    // Recupera o token
    
    private String recoverToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }

        return authHeader.substring(7);
    }
}