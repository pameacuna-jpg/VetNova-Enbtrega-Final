package com.vetnova.auth.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 1. Deshabilitamos CSRF (No es necesario en APIs REST con JWT)
            .csrf(csrf -> csrf.disable())
            
            // 2. Establecemos arquitectura Stateless (Sin estado/sesión)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // 3. Configuramos qué rutas son públicas y cuáles privadas
            .authorizeHttpRequests(auth -> auth
                // Se usa /** para que TANTO el login como recuperar-password sean públicos
                .requestMatchers("/api/v1/auth/**").permitAll() 
                .anyRequest().authenticated() // Todo lo demás exige estar autenticado (con Token)
            );

        return http.build();
    }

    // Encriptador de contraseñas para cuando tengamos que compararlas
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}