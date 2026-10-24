package com.agendasalas.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
public class SecurityConfig {
    private final SecurityErrorHandler securityErrorHandler;

    public SecurityConfig(SecurityErrorHandler securityErrorHandler) {
        this.securityErrorHandler = securityErrorHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.GET, "/").permitAll()

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/auth/registro"
                        ).permitAll()

                        .requestMatchers(
                                "/api/usuarios",
                                "/api/usuarios/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/salas",
                                "/api/salas/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/salas",
                                "/api/salas/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/salas",
                                "/api/salas/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                "/api/salas",
                                "/api/salas/**",
                                "/api/reservas",
                                "/api/reservas/**"
                        ).authenticated()

                        .anyRequest().authenticated()
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(
                                securityErrorHandler.authenticationEntryPoint()
                        )
                        .accessDeniedHandler(
                                securityErrorHandler.accessDeniedHandler()
                        )
                )
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}