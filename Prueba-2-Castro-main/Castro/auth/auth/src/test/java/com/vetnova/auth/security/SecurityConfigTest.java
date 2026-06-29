package com.vetnova.auth.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SecurityConfigTest {

    @Test
    void passwordEncoder_DebeRetornarBCryptPasswordEncoder() {
        // Given
        SecurityConfig config = new SecurityConfig();

        // When
        PasswordEncoder encoder = config.passwordEncoder();

        // Then
        assertNotNull(encoder);
        assertTrue(encoder instanceof BCryptPasswordEncoder);
    }

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    void securityFilterChain_DebeConfigurarRutasYRetornarCadena() throws Exception {
        // Given
        SecurityConfig config = new SecurityConfig();

        HttpSecurity http = mock(HttpSecurity.class, RETURNS_DEEP_STUBS);
        
        // CORRECCIÓN: Usamos DefaultSecurityFilterChain para que coincida exactamente con lo que retorna http.build()
        DefaultSecurityFilterChain filterChain = mock(DefaultSecurityFilterChain.class);

        when(http.build()).thenReturn(filterChain);

        when(http.csrf(any(Customizer.class))).thenAnswer(invocation -> {
            Customizer customizer = invocation.getArgument(0);
            CsrfConfigurer csrfMock = mock(CsrfConfigurer.class, RETURNS_DEEP_STUBS);
            customizer.customize(csrfMock); 
            return http;
        });

        when(http.authorizeHttpRequests(any(Customizer.class))).thenAnswer(invocation -> {
            Customizer customizer = invocation.getArgument(0);
            AuthorizeHttpRequestsConfigurer.AuthorizationManagerRequestMatcherRegistry registryMock =
                    mock(AuthorizeHttpRequestsConfigurer.AuthorizationManagerRequestMatcherRegistry.class, RETURNS_DEEP_STUBS);
            customizer.customize(registryMock); 
            return http;
        });

        // When
        SecurityFilterChain result = config.securityFilterChain(http);

        // Then
        assertNotNull(result);
        assertEquals(filterChain, result);
    }
}