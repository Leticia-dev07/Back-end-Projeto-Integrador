package com.senac.pi.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private SecurityFilter securityFilter;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable()) // Desabilita CSRF para APIs Stateless[cite: 10]
            .cors(Customizer.withDefaults()) // Aplica as configurações do Bean corsConfigurationSource
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // JWT não usa sessão
            .authorizeHttpRequests(authorize -> authorize
                // Endpoints Públicos
                .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/auth/register").permitAll() // Caso tenha registro público
                
                // Liberação do Swagger UI
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                
                .requestMatchers("/actuator/**").permitAll()
                
                // Permissões para Alunos
                .requestMatchers(HttpMethod.GET, "/alunos/**").hasAnyRole("ADMIN", "COORDENADOR")
                .requestMatchers(HttpMethod.POST, "/alunos/**").hasAnyRole("ADMIN", "COORDENADOR")
                .requestMatchers(HttpMethod.DELETE, "/alunos/**").hasRole("ADMIN")
                
                // Permissões para Coordenadores
                .requestMatchers(HttpMethod.GET, "/coordenadores/**").hasAnyRole("ADMIN", "COORDENADOR")
                .requestMatchers(HttpMethod.POST, "/coordenadores/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/coordenadores/**").hasRole("ADMIN")

                // Qualquer outra requisição exige autenticação
                .anyRequest().authenticated()
            )
            .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Origens permitidas (Live Server e Localhost)
        configuration.setAllowedOrigins(Arrays.asList(
            "http://127.0.0.1:5500",
            "http://localhost:5500",
            "http://localhost:3000" 
        ));

        // Métodos HTTP permitidos
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS"
        ));

        // Cabeçalhos permitidos para o JWT funcionar
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization", "Content-Type", "Accept"
        ));

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}