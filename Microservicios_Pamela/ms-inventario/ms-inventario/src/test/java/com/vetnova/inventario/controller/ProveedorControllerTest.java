package com.vetnova.inventario.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vetnova.inventario.model.Proveedor;
import com.vetnova.inventario.service.ProveedorService;
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

@WebMvcTest(ProveedorController.class)
class ProveedorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProveedorService proveedorService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void listarProveedores_debeRetornarLista() throws Exception {

        Mockito.when(proveedorService.listarProveedores())
                .thenReturn(List.of(crearProveedor()));

        mockMvc.perform(get("/api/v1/proveedores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idProveedor").value(1));
    }

    @Test
    void buscarPorId_debeRetornarProveedor() throws Exception {

        Mockito.when(proveedorService.buscarPorId(1L))
                .thenReturn(crearProveedor());

        mockMvc.perform(get("/api/v1/proveedores/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Proveedor Test"));
    }

    @Test
    void crearProveedor_debeRetornarOk() throws Exception {

        Proveedor proveedor = crearProveedor();

        Mockito.when(proveedorService.crearProveedor(any(Proveedor.class)))
                .thenReturn(proveedor);

        mockMvc.perform(post("/api/v1/proveedores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(proveedor)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idProveedor").value(1));
    }

    @Test
    void actualizarProveedor_debeRetornarProveedorActualizado() throws Exception {

        Proveedor proveedor = crearProveedor();

        Mockito.when(proveedorService.actualizarProveedor(eq(1L), any(Proveedor.class)))
                .thenReturn(proveedor);

        mockMvc.perform(put("/api/v1/proveedores/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(proveedor)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Proveedor Test"));
    }

    @Test
    void eliminarProveedor_debeRetornarMensaje() throws Exception {

        Mockito.doNothing()
                .when(proveedorService)
                .eliminarProveedor(1L);

        mockMvc.perform(delete("/api/v1/proveedores/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Proveedor eliminado correctamente"));
    }

    @Test
    void buscarPorNombre_debeRetornarLista() throws Exception {

        Mockito.when(proveedorService.buscarPorNombre("Proveedor"))
                .thenReturn(List.of(crearProveedor()));

        mockMvc.perform(get("/api/v1/proveedores/buscar/Proveedor"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Proveedor Test"));
    }

    private Proveedor crearProveedor() {

        Proveedor proveedor = new Proveedor();

        proveedor.setIdProveedor(1L);
        proveedor.setNombre("Proveedor Test");
        proveedor.setTelefono("56912345678");
        proveedor.setEmail("proveedor@test.cl");
        proveedor.setDireccion("Concepción");
        proveedor.setActivo(true);

        return proveedor;
    }
}
