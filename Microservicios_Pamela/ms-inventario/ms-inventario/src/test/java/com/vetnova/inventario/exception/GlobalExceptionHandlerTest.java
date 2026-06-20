package com.vetnova.inventario.exception;

import com.vetnova.inventario.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

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
                .thenReturn("/api/v1/productos/99");
    }

    @Test
    void manejarRuntimeException_cuandoMensajeContieneNoEncontrado_debeRetornarNotFound() {

        RuntimeException exception =
                new RuntimeException("Producto no encontrado con ID: 99");

        ErrorResponse response =
                handler.manejarRuntimeException(exception, request);

        assertNotNull(response);

        assertEquals(
                HttpStatus.NOT_FOUND.value(),
                response.getEstado()
        );

        assertEquals(
                "Recurso no encontrado",
                response.getError()
        );

        assertEquals(
                "Producto no encontrado con ID: 99",
                response.getMensaje()
        );

        assertEquals(
                "/api/v1/productos/99",
                response.getRuta()
        );

        assertNotNull(response.getFecha());
    }

    @Test
    void manejarRuntimeException_cuandoEsErrorGeneral_debeRetornarBadRequest() {

        RuntimeException exception =
                new RuntimeException("Stock insuficiente para realizar la salida");

        ErrorResponse response =
                handler.manejarRuntimeException(exception, request);

        assertNotNull(response);

        assertEquals(
                HttpStatus.BAD_REQUEST.value(),
                response.getEstado()
        );

        assertEquals(
                "Error en la solicitud",
                response.getError()
        );

        assertEquals(
                "Stock insuficiente para realizar la salida",
                response.getMensaje()
        );

        assertEquals(
                "/api/v1/productos/99",
                response.getRuta()
        );

        assertNotNull(response.getFecha());
    }
}