package com.vetnova.sucursales.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vetnova.sucursales.model.HorarioSucursal;
import com.vetnova.sucursales.service.HorarioSucursalService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HorarioSucursalController.class)
class HorarioSucursalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HorarioSucursalService horarioSucursalService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void listarHorarios_debeRetornarLista() throws Exception {
        Mockito.when(horarioSucursalService.listarHorarios())
                .thenReturn(List.of(crearHorario()));

        mockMvc.perform(get("/api/v1/horarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idHorario").value(1));
    }

    @Test
    void listarActivos_debeRetornarLista() throws Exception {
        Mockito.when(horarioSucursalService.listarActivos())
                .thenReturn(List.of(crearHorario()));

        mockMvc.perform(get("/api/v1/horarios/activos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].activo").value(true));
    }

    @Test
    void buscarPorId_debeRetornarHorario() throws Exception {
        Mockito.when(horarioSucursalService.buscarPorId(1L))
                .thenReturn(crearHorario());

        mockMvc.perform(get("/api/v1/horarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dia").value("Lunes"));
    }

    @Test
    void crearHorario_debeRetornarOk() throws Exception {
        HorarioSucursal horario = crearHorario();

        Mockito.when(horarioSucursalService.crearHorario(any(HorarioSucursal.class)))
                .thenReturn(horario);

        mockMvc.perform(post("/api/v1/horarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(horario)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idHorario").value(1));
    }

    @Test
    void actualizarHorario_debeRetornarHorario() throws Exception {
        HorarioSucursal horario = crearHorario();

        Mockito.when(horarioSucursalService.actualizarHorario(eq(1L), any(HorarioSucursal.class)))
                .thenReturn(horario);

        mockMvc.perform(put("/api/v1/horarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(horario)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.horaApertura").value("09:00"));
    }

    @Test
    void desactivarHorario_debeRetornarMensaje() throws Exception {
        Mockito.doNothing().when(horarioSucursalService).desactivarHorario(1L);

        mockMvc.perform(delete("/api/v1/horarios/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Horario desactivado correctamente"));
    }

    @Test
    void buscarPorSucursal_debeRetornarLista() throws Exception {
        Mockito.when(horarioSucursalService.buscarPorSucursal(1L))
                .thenReturn(List.of(crearHorario()));

        mockMvc.perform(get("/api/v1/horarios/sucursal/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idSucursal").value(1));
    }

    @Test
    void crearHorario_conDatosInvalidos_debeRetornarBadRequest() throws Exception {
        HorarioSucursal horario = new HorarioSucursal();

        mockMvc.perform(post("/api/v1/horarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(horario)))
                .andExpect(status().isBadRequest());
    }

    private HorarioSucursal crearHorario() {
        HorarioSucursal horario = new HorarioSucursal();
        horario.setIdHorario(1L);
        horario.setDia("Lunes");
        horario.setHoraApertura("09:00");
        horario.setHoraCierre("18:00");
        horario.setIdSucursal(1L);
        horario.setActivo(true);
        return horario;
    }
}
