package com.vetnova.sucursales.exception;

import com.vetnova.sucursales.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        request = mock(HttpServletRequest.class);

        when(request.getRequestURI())
                .thenReturn("/api/v1/sucursales/99");
    }

    @Test
    void manejarRuntimeException_debeRetornarNotFound() {
        RuntimeException ex =
                new RuntimeException("Sucursal no encontrada");

        ErrorResponse response =
                handler.manejarRuntimeException(ex, request);

        assertNotNull(response);
        assertEquals(404, response.getEstado());
        assertEquals("Recurso no encontrado", response.getError());
        assertEquals("Sucursal no encontrada", response.getMensaje());
        assertEquals("/api/v1/sucursales/99", response.getRuta());
    }
}
