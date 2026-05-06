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
            // desabilitar o csrf
            .csrf(csrf -> csrf.disable())

            // cors
            .cors(Customizer.withDefaults())

            // Correção do iframe do pdf
            .headers(headers -> 
                headers.frameOptions(frame -> frame.sameOrigin())
            )

            // Stateless JWT
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // Autorizações
            .authorizeHttpRequests(authorize -> authorize

                // End Points publicos

                .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/auth/register").permitAll()
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                
                .requestMatchers("/certificados/**").permitAll()

                // Liberar o Actuator
                .requestMatchers("/actuator/**").permitAll()

                // End Points protegidos

                .requestMatchers(HttpMethod.GET, "/submissoes/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/cursos/**").authenticated()

                // Alunos

                .requestMatchers(HttpMethod.GET, "/alunos/**")
                    .hasAnyRole("ADMIN", "COORDENADOR", "ALUNO")

                .requestMatchers(HttpMethod.POST, "/alunos/**")
                    .hasAnyRole("ADMIN", "COORDENADOR")

                // Permissão para descvincular o aluno do curso
                .requestMatchers(HttpMethod.DELETE, "/alunos/{alunoId}/cursos/{cursoId}")
                    .hasAnyRole("ADMIN", "COORDENADOR")

                // Exclusão do aluno
                .requestMatchers(HttpMethod.DELETE, "/alunos/**")
                    .hasRole("ADMIN")

                // Coordenadores

                .requestMatchers(HttpMethod.GET, "/coordenadores/**")
                    .hasAnyRole("ADMIN", "COORDENADOR")

                // Qualquer outra rota
  
                .anyRequest().authenticated()
            )

            // Filtro JWT
            .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)

            .build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Origens permitidas pelo front end
        configuration.setAllowedOrigins(Arrays.asList(
            "http://127.0.0.1:5500",
            "http://localhost:5500",
            "http://localhost:3000"
        ));

        // Metodos
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS"
        ));

        // Header permitidos
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "Accept"
        ));

        // Importante para o JWT
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