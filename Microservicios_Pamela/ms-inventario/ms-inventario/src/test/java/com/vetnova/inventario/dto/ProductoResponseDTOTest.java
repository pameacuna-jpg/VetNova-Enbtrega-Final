package com.vetnova.inventario.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductoResponseDTOTest {

    @Test
    void constructorConArgumentos_debeAsignarTodosLosCampos() {
        ProductoResponseDTO dto = new ProductoResponseDTO(
                1L,
                "Vacuna Rabia",
                "Vacunas",
                15000,
                20,
                5,
                true
        );

        assertEquals(1L, dto.getIdProducto());
        assertEquals("Vacuna Rabia", dto.getNombre());
        assertEquals("Vacunas", dto.getCategoria());
        assertEquals(15000, dto.getPrecio());
        assertEquals(20, dto.getStockActual());
        assertEquals(5, dto.getStockMinimo());
        assertTrue(dto.getActivo());
    }

    @Test
    void constructorVacioYSetters_debenAsignarValoresCorrectamente() {
        ProductoResponseDTO dto = new ProductoResponseDTO();

        dto.setIdProducto(2L);
        dto.setNombre("Alimento Premium");
        dto.setCategoria("Alimentos");
        dto.setPrecio(25000);
        dto.setStockActual(10);
        dto.setStockMinimo(3);
        dto.setActivo(true);

        assertEquals(2L, dto.getIdProducto());
        assertEquals("Alimento Premium", dto.getNombre());
        assertEquals("Alimentos", dto.getCategoria());
        assertEquals(25000, dto.getPrecio());
        assertEquals(10, dto.getStockActual());
        assertEquals(3, dto.getStockMinimo());
        assertTrue(dto.getActivo());
    }
}
