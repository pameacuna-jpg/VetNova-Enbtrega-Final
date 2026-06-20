package com.vetnova.inventario.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vetnova.inventario.dto.ProductoRequestDTO;
import com.vetnova.inventario.dto.ProductoResponseDTO;
import com.vetnova.inventario.service.ProductoService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductoController.class)
class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductoService productoService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void listarProductos_debeRetornarListaYStatusOk() throws Exception {
        ProductoResponseDTO producto = new ProductoResponseDTO(
                1L,
                "Vacuna Rabia",
                "Vacunas",
                15000,
                20,
                5,
                true
        );

        Mockito.when(productoService.listarProductos())
                .thenReturn(List.of(producto));

        mockMvc.perform(get("/api/v1/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idProducto").value(1L))
                .andExpect(jsonPath("$[0].nombre").value("Vacuna Rabia"))
                .andExpect(jsonPath("$[0].categoria").value("Vacunas"))
                .andExpect(jsonPath("$[0].precio").value(15000))
                .andExpect(jsonPath("$[0].stockActual").value(20))
                .andExpect(jsonPath("$[0].stockMinimo").value(5))
                .andExpect(jsonPath("$[0].activo").value(true));
    }

    @Test
    void buscarPorId_debeRetornarProductoYStatusOk() throws Exception {
        ProductoResponseDTO producto = new ProductoResponseDTO(
                1L,
                "Vacuna Rabia",
                "Vacunas",
                15000,
                20,
                5,
                true
        );

        Mockito.when(productoService.buscarPorId(1L))
                .thenReturn(producto);

        mockMvc.perform(get("/api/v1/productos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idProducto").value(1L))
                .andExpect(jsonPath("$.nombre").value("Vacuna Rabia"))
                .andExpect(jsonPath("$.categoria").value("Vacunas"));
    }

    @Test
    void crearProducto_debeRetornarCreated() throws Exception {
        ProductoRequestDTO request = crearRequestValido();

        ProductoResponseDTO response = new ProductoResponseDTO(
                1L,
                "Vacuna Rabia",
                "Vacunas",
                15000,
                20,
                5,
                true
        );

        Mockito.when(productoService.crearProducto(any(ProductoRequestDTO.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idProducto").value(1L))
                .andExpect(jsonPath("$.nombre").value("Vacuna Rabia"))
                .andExpect(jsonPath("$.precio").value(15000));
    }

    @Test
    void actualizarProducto_debeRetornarOk() throws Exception {
        ProductoRequestDTO request = crearRequestValido();

        ProductoResponseDTO response = new ProductoResponseDTO(
                1L,
                "Vacuna Rabia Actualizada",
                "Vacunas",
                18000,
                25,
                5,
                true
        );

        Mockito.when(productoService.actualizarProducto(eq(1L), any(ProductoRequestDTO.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/v1/productos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idProducto").value(1L))
                .andExpect(jsonPath("$.nombre").value("Vacuna Rabia Actualizada"))
                .andExpect(jsonPath("$.precio").value(18000))
                .andExpect(jsonPath("$.stockActual").value(25));
    }

    @Test
    void eliminarProducto_debeRetornarNoContent() throws Exception {
        Mockito.doNothing().when(productoService).eliminarProducto(1L);

        mockMvc.perform(delete("/api/v1/productos/1"))
                .andExpect(status().isNoContent());

        Mockito.verify(productoService).eliminarProducto(1L);
    }

    @Test
    void buscarPorCategoria_debeRetornarListaYStatusOk() throws Exception {
        ProductoResponseDTO producto = new ProductoResponseDTO(
                2L,
                "Alimento Premium",
                "Alimentos",
                25000,
                10,
                3,
                true
        );

        Mockito.when(productoService.buscarPorCategoria("Alimentos"))
                .thenReturn(List.of(producto));

        mockMvc.perform(get("/api/v1/productos/categoria/Alimentos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idProducto").value(2L))
                .andExpect(jsonPath("$[0].nombre").value("Alimento Premium"))
                .andExpect(jsonPath("$[0].categoria").value("Alimentos"));
    }

    @Test
    void listarProductosBajoStock_debeRetornarListaYStatusOk() throws Exception {
        ProductoResponseDTO producto = new ProductoResponseDTO(
                3L,
                "Jeringa",
                "Insumos",
                500,
                2,
                5,
                true
        );

        Mockito.when(productoService.listarProductosBajoStock())
                .thenReturn(List.of(producto));

        mockMvc.perform(get("/api/v1/productos/bajo-stock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idProducto").value(3L))
                .andExpect(jsonPath("$[0].nombre").value("Jeringa"))
                .andExpect(jsonPath("$[0].stockActual").value(2))
                .andExpect(jsonPath("$[0].stockMinimo").value(5));
    }

    @Test
    void crearProducto_conDatosInvalidos_debeRetornarBadRequest() throws Exception {
        ProductoRequestDTO request = new ProductoRequestDTO();
        request.setNombre("");
        request.setCategoria("");
        request.setPrecio(-1);
        request.setStockActual(-5);
        request.setStockMinimo(-2);

        mockMvc.perform(post("/api/v1/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    private ProductoRequestDTO crearRequestValido() {
        ProductoRequestDTO request = new ProductoRequestDTO();
        request.setNombre("Vacuna Rabia");
        request.setCategoria("Vacunas");
        request.setPrecio(15000);
        request.setStockActual(20);
        request.setStockMinimo(5);
        return request;
    }
}
