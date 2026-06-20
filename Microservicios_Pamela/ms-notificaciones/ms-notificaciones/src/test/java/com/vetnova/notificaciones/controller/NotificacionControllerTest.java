package com.vetnova.notificaciones.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vetnova.notificaciones.dto.NotificacionRequestDTO;
import com.vetnova.notificaciones.dto.NotificacionResponseDTO;
import com.vetnova.notificaciones.service.NotificacionService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificacionController.class)
class NotificacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificacionService notificacionService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void listarNotificaciones_debeRetornarLista() throws Exception {

        NotificacionResponseDTO dto = new NotificacionResponseDTO(
                1L,
                "cliente@correo.cl",
                "Stock bajo",
                "STOCK_BAJO",
                "PENDIENTE",
                "EMAIL",
                "ALTA"
        );

        Mockito.when(notificacionService.listarNotificaciones())
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/api/v1/notificaciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idNotificacion").value(1))
                .andExpect(jsonPath("$[0].destinatario").value("cliente@correo.cl"));
    }

    @Test
    void buscarPorId_debeRetornarNotificacion() throws Exception {

        NotificacionResponseDTO dto = new NotificacionResponseDTO(
                1L,
                "cliente@correo.cl",
                "Stock bajo",
                "STOCK_BAJO",
                "PENDIENTE",
                "EMAIL",
                "ALTA"
        );

        Mockito.when(notificacionService.buscarPorId(1L))
                .thenReturn(dto);

        mockMvc.perform(get("/api/v1/notificaciones/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idNotificacion").value(1))
                .andExpect(jsonPath("$.tipo").value("STOCK_BAJO"));
    }

    @Test
    void crearNotificacion_debeRetornarCreated() throws Exception {

        NotificacionRequestDTO request = new NotificacionRequestDTO();
        request.setDestinatario("cliente@correo.cl");
        request.setMensaje("Stock bajo");
        request.setTipo("STOCK_BAJO");

        NotificacionResponseDTO response = new NotificacionResponseDTO(
                1L,
                "cliente@correo.cl",
                "Stock bajo",
                "STOCK_BAJO",
                "PENDIENTE",
                "EMAIL",
                "ALTA"
        );

        Mockito.when(notificacionService.crearNotificacion(any()))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/notificaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idNotificacion").value(1))
                .andExpect(jsonPath("$.tipo").value("STOCK_BAJO"));
    }

    @Test
    void actualizarNotificacion_debeRetornarOk() throws Exception {

        NotificacionRequestDTO request = new NotificacionRequestDTO();
        request.setDestinatario("cliente@correo.cl");
        request.setMensaje("Actualizada");
        request.setTipo("STOCK_BAJO");

        NotificacionResponseDTO response = new NotificacionResponseDTO(
                1L,
                "cliente@correo.cl",
                "Actualizada",
                "STOCK_BAJO",
                "PENDIENTE",
                "EMAIL",
                "ALTA"
        );

        Mockito.when(
                notificacionService.actualizarNotificacion(
                        eq(1L),
                        any(NotificacionRequestDTO.class)
                )
        ).thenReturn(response);

        mockMvc.perform(put("/api/v1/notificaciones/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Actualizada"));
    }

    @Test
    void marcarEnviada_debeRetornarNoContent() throws Exception {

        Mockito.doNothing()
                .when(notificacionService)
                .marcarEnviada(1L);

        mockMvc.perform(patch("/api/v1/notificaciones/enviar/1"))
                .andExpect(status().isNoContent());

        Mockito.verify(notificacionService)
                .marcarEnviada(1L);
    }

    @Test
    void buscarPorEstado_debeRetornarLista() throws Exception {

        NotificacionResponseDTO dto = new NotificacionResponseDTO(
                1L,
                "cliente@correo.cl",
                "Stock bajo",
                "STOCK_BAJO",
                "PENDIENTE",
                "EMAIL",
                "ALTA"
        );

        Mockito.when(notificacionService.buscarPorEstado("PENDIENTE"))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/api/v1/notificaciones/estado/PENDIENTE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].estado").value("PENDIENTE"));
    }

    @Test
    void crearNotificacionInvalida_debeRetornarBadRequest() throws Exception {

        NotificacionRequestDTO request = new NotificacionRequestDTO();

        mockMvc.perform(post("/api/v1/notificaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
