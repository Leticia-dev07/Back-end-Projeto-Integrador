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
            // 🔒 Desabilita CSRF (API stateless)
            .csrf(csrf -> csrf.disable())

            // 🌐 CORS
            .cors(Customizer.withDefaults())

            // 🔥 CORREÇÃO DO IFRAME (PDF)
            .headers(headers -> 
                headers.frameOptions(frame -> frame.sameOrigin())
            )

            // 🔐 Stateless (JWT)
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // 🔑 AUTORIZAÇÃO
            .authorizeHttpRequests(authorize -> authorize

                // ========================
                // 🔓 ENDPOINTS PÚBLICOS
                // ========================
                .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/auth/register").permitAll()
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                
                .requestMatchers("/certificados/**").permitAll()

                // (OPCIONAL) liberar certificados - cuidado com segurança
                // .requestMatchers(HttpMethod.GET, "/certificados/**").permitAll()

                // ========================
                // 🔒 ENDPOINTS PROTEGIDOS
                // ========================
                .requestMatchers(HttpMethod.GET, "/submissoes/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/cursos/**").authenticated()

                // ========================
                // 👤 ALUNOS
                // ========================
                .requestMatchers(HttpMethod.GET, "/alunos/**")
                    .hasAnyRole("ADMIN", "COORDENADOR", "ALUNO")

                .requestMatchers(HttpMethod.POST, "/alunos/**")
                    .hasAnyRole("ADMIN", "COORDENADOR")

                .requestMatchers(HttpMethod.DELETE, "/alunos/**")
                    .hasRole("ADMIN")

                // ========================
                // 👨‍🏫 COORDENADORES
                // ========================
                .requestMatchers(HttpMethod.GET, "/coordenadores/**")
                    .hasAnyRole("ADMIN", "COORDENADOR")

                // ========================
                // 🔐 QUALQUER OUTRA ROTA
                // ========================
                .anyRequest().authenticated()
            )

            // 🔥 FILTRO JWT
            .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)

            .build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 🌍 ORIGENS PERMITIDAS (FRONTEND)
        configuration.setAllowedOrigins(Arrays.asList(
            "http://127.0.0.1:5500",
            "http://localhost:5500",
            "http://localhost:3000"
        ));

        // 📡 MÉTODOS
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS"
        ));

        // 📦 HEADERS PERMITIDOS
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "Accept"
        ));

        // 🔥 IMPORTANTE PRA JWT
        configuration.setExposedHeaders(Arrays.asList(
            "Authorization"
        ));

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}