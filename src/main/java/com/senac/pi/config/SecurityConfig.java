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

            // Ativa o CORS com as configurações definidas abaixo no bean corsConfigurationSource
            .cors(Customizer.withDefaults())

            // Correção do iframe para visualização de PDF e H2 Console
            .headers(headers -> 
                headers.frameOptions(frame -> frame.sameOrigin())
            )

            // Configuração Stateless para API REST com JWT
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // Definição de permissões de rotas
            .authorizeHttpRequests(authorize -> authorize

                // Endpoints públicos (Login, Registro, Swagger e Documentação)
                .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/auth/register").permitAll()
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                .requestMatchers("/certificados/**").permitAll()

                // Liberar o Actuator para monitoramento do Render
                .requestMatchers("/actuator/**").permitAll()

                // Endpoints que exigem autenticação genérica
                .requestMatchers(HttpMethod.GET, "/submissoes/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/cursos/**").authenticated()

                // Regras de Alunos por Perfil
                .requestMatchers(HttpMethod.GET, "/alunos/**")
                    .hasAnyRole("ADMIN", "COORDENADOR", "ALUNO")

                .requestMatchers(HttpMethod.POST, "/alunos/**")
                    .hasAnyRole("ADMIN", "COORDENADOR")

                .requestMatchers(HttpMethod.DELETE, "/alunos/{alunoId}/cursos/{cursoId}")
                    .hasAnyRole("ADMIN", "COORDENADOR")

                .requestMatchers(HttpMethod.DELETE, "/alunos/**")
                    .hasRole("ADMIN")

                // Regras de Coordenadores
                .requestMatchers(HttpMethod.GET, "/coordenadores/**")
                    .hasAnyRole("ADMIN", "COORDENADOR")

                // Qualquer outra requisição precisa estar logado
                .anyRequest().authenticated()
            )

            // Adiciona o seu filtro de segurança JWT antes do filtro padrão do Spring
            .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)

            .build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // MODO "LIBERAR GERAL": Permite qualquer origem (URL) acessar a API.
        // Usamos OriginPatterns porque allowCredentials(true) não permite o caractere "*" puro.
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));

        // Métodos HTTP permitidos
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS"
        ));

        // Cabeçalhos permitidos nas requisições
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "Accept",
            "X-Requested-With"
        ));

        // Expõe o cabeçalho Authorization para que o Front-end consiga ler o Token JWT
        configuration.setExposedHeaders(Arrays.asList(
            "Authorization"
        ));

        // Permite o envio de cookies e autenticação (necessário para JWT em muitos cenários)
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