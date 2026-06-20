package com.vetnova.sucursales.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vetnova.sucursales.model.BoxAtencion;
import com.vetnova.sucursales.service.BoxAtencionService;
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

@WebMvcTest(BoxAtencionController.class)
class BoxAtencionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BoxAtencionService boxAtencionService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void listarBoxes_debeRetornarLista() throws Exception {
        Mockito.when(boxAtencionService.listarBoxes())
                .thenReturn(List.of(crearBox()));

        mockMvc.perform(get("/api/v1/boxes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idBox").value(1));
    }

    @Test
    void listarDisponibles_debeRetornarLista() throws Exception {
        Mockito.when(boxAtencionService.listarDisponibles())
                .thenReturn(List.of(crearBox()));

        mockMvc.perform(get("/api/v1/boxes/disponibles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].disponible").value(true));
    }

    @Test
    void buscarPorId_debeRetornarBox() throws Exception {
        Mockito.when(boxAtencionService.buscarPorId(1L))
                .thenReturn(crearBox());

        mockMvc.perform(get("/api/v1/boxes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Box 1"));
    }

    @Test
    void crearBox_debeRetornarOk() throws Exception {
        BoxAtencion box = crearBox();

        Mockito.when(boxAtencionService.crearBox(any(BoxAtencion.class)))
                .thenReturn(box);

        mockMvc.perform(post("/api/v1/boxes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(box)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idBox").value(1));
    }

    @Test
    void actualizarBox_debeRetornarBox() throws Exception {
        BoxAtencion box = crearBox();

        Mockito.when(boxAtencionService.actualizarBox(eq(1L), any(BoxAtencion.class)))
                .thenReturn(box);

        mockMvc.perform(put("/api/v1/boxes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(box)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipoAtencion").value("Consulta"));
    }

    @Test
    void desactivarBox_debeRetornarMensaje() throws Exception {
        Mockito.doNothing().when(boxAtencionService).desactivarBox(1L);

        mockMvc.perform(delete("/api/v1/boxes/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Box desactivado correctamente"));
    }

    @Test
    void buscarPorSucursal_debeRetornarLista() throws Exception {
        Mockito.when(boxAtencionService.buscarPorSucursal(1L))
                .thenReturn(List.of(crearBox()));

        mockMvc.perform(get("/api/v1/boxes/sucursal/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idSucursal").value(1));
    }

    @Test
    void crearBox_conDatosInvalidos_debeRetornarBadRequest() throws Exception {
        BoxAtencion box = new BoxAtencion();

        mockMvc.perform(post("/api/v1/boxes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(box)))
                .andExpect(status().isBadRequest());
    }

    private BoxAtencion crearBox() {
        BoxAtencion box = new BoxAtencion();
        box.setIdBox(1L);
        box.setNombre("Box 1");
        box.setTipoAtencion("Consulta");
        box.setIdSucursal(1L);
        box.setDisponible(true);
        return box;
    }
}