package com.vetnova.sucursales.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vetnova.sucursales.model.Sucursal;
import com.vetnova.sucursales.service.SucursalService;
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

@WebMvcTest(SucursalController.class)
class SucursalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SucursalService sucursalService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void listarSucursales_debeRetornarLista() throws Exception {
        Mockito.when(sucursalService.listarSucursales())
                .thenReturn(List.of(crearSucursal()));

        mockMvc.perform(get("/api/v1/sucursales"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idSucursal").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Sucursal Centro"));
    }

    @Test
    void listarSucursalesActivas_debeRetornarLista() throws Exception {
        Mockito.when(sucursalService.listarSucursalesActivas())
                .thenReturn(List.of(crearSucursal()));

        mockMvc.perform(get("/api/v1/sucursales/activas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].estado").value("ACTIVA"));
    }

    @Test
    void buscarPorId_debeRetornarSucursal() throws Exception {
        Mockito.when(sucursalService.buscarPorId(1L))
                .thenReturn(crearSucursal());

        mockMvc.perform(get("/api/v1/sucursales/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ciudad").value("Concepción"));
    }

    @Test
    void crearSucursal_debeRetornarOk() throws Exception {
        Sucursal sucursal = crearSucursal();

        Mockito.when(sucursalService.crearSucursal(any(Sucursal.class)))
                .thenReturn(sucursal);

        mockMvc.perform(post("/api/v1/sucursales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sucursal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idSucursal").value(1));
    }

    @Test
    void actualizarSucursal_debeRetornarSucursal() throws Exception {
        Sucursal sucursal = crearSucursal();

        Mockito.when(sucursalService.actualizarSucursal(eq(1L), any(Sucursal.class)))
                .thenReturn(sucursal);

        mockMvc.perform(put("/api/v1/sucursales/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sucursal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Sucursal Centro"));
    }

    @Test
    void desactivarSucursal_debeRetornarMensaje() throws Exception {
        Mockito.doNothing().when(sucursalService).desactivarSucursal(1L);

        mockMvc.perform(delete("/api/v1/sucursales/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Sucursal desactivada correctamente"));
    }

    @Test
    void buscarPorCiudad_debeRetornarLista() throws Exception {
        Mockito.when(sucursalService.buscarPorCiudad("Concepción"))
                .thenReturn(List.of(crearSucursal()));

        mockMvc.perform(get("/api/v1/sucursales/ciudad/Concepción"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ciudad").value("Concepción"));
    }

    @Test
    void crearSucursal_conDatosInvalidos_debeRetornarBadRequest() throws Exception {
        Sucursal sucursal = new Sucursal();

        mockMvc.perform(post("/api/v1/sucursales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sucursal)))
                .andExpect(status().isBadRequest());
    }

    private Sucursal crearSucursal() {
        Sucursal sucursal = new Sucursal();
        sucursal.setIdSucursal(1L);
        sucursal.setNombre("Sucursal Centro");
        sucursal.setDireccion("Av. Principal 123");
        sucursal.setTelefono("56912345678");
        sucursal.setCiudad("Concepción");
        sucursal.setEstado("ACTIVA");
        return sucursal;
    }
}