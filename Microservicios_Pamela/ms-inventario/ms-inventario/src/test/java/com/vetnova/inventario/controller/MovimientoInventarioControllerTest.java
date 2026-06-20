package com.vetnova.inventario.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vetnova.inventario.model.MovimientoInventario;
import com.vetnova.inventario.service.MovimientoInventarioService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MovimientoInventarioController.class)
class MovimientoInventarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MovimientoInventarioService movimientoService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void listarMovimientos_debeRetornarLista() throws Exception {

        Mockito.when(movimientoService.listarMovimientos())
                .thenReturn(List.of(crearMovimiento()));

        mockMvc.perform(get("/api/v1/movimientos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idMovimiento").value(1));
    }

    @Test
    void buscarPorId_debeRetornarMovimiento() throws Exception {

        Mockito.when(movimientoService.buscarPorId(1L))
                .thenReturn(crearMovimiento());

        mockMvc.perform(get("/api/v1/movimientos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idProducto").value(10));
    }

    @Test
    void registrarMovimiento_debeRetornarOk() throws Exception {

        MovimientoInventario movimiento = crearMovimiento();

        Mockito.when(
                movimientoService.registrarMovimiento(any(MovimientoInventario.class)))
                .thenReturn(movimiento);

        mockMvc.perform(post("/api/v1/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movimiento)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idMovimiento").value(1));
    }

    @Test
    void buscarPorProducto_debeRetornarLista() throws Exception {

        Mockito.when(movimientoService.buscarPorProducto(10L))
                .thenReturn(List.of(crearMovimiento()));

        mockMvc.perform(get("/api/v1/movimientos/producto/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idProducto").value(10));
    }

    @Test
    void buscarPorSucursal_debeRetornarLista() throws Exception {

        Mockito.when(movimientoService.buscarPorSucursal(1L))
                .thenReturn(List.of(crearMovimiento()));

        mockMvc.perform(get("/api/v1/movimientos/sucursal/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idSucursal").value(1));
    }

    @Test
    void buscarPorTipo_debeRetornarLista() throws Exception {

        Mockito.when(movimientoService.buscarPorTipo("ENTRADA"))
                .thenReturn(List.of(crearMovimiento()));

        mockMvc.perform(get("/api/v1/movimientos/tipo/ENTRADA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tipoMovimiento").value("ENTRADA"));
    }

    private MovimientoInventario crearMovimiento() {

        MovimientoInventario movimiento = new MovimientoInventario();

        movimiento.setIdMovimiento(1L);
        movimiento.setIdProducto(10L);
        movimiento.setIdSucursal(1L);
        movimiento.setTipoMovimiento("ENTRADA");
        movimiento.setCantidad(5);
        movimiento.setObservacion("Ingreso inicial");

        return movimiento;
    }
}