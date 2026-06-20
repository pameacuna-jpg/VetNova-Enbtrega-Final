package com.vetnova.inventario.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ProductoRequestDTOTest {

    @Test
    void productoRequestDTO_conDatosValidos_noDebeTenerErroresDeValidacion() {
        ProductoRequestDTO dto = new ProductoRequestDTO();
        dto.setNombre("Vacuna Rabia");
        dto.setCategoria("Vacunas");
        dto.setPrecio(15000);
        dto.setStockActual(20);
        dto.setStockMinimo(5);

        Set<ConstraintViolation<ProductoRequestDTO>> errores = validar(dto);

        assertTrue(errores.isEmpty());
    }

    @Test
    void productoRequestDTO_conDatosInvalidos_debeTenerErroresDeValidacion() {
        ProductoRequestDTO dto = new ProductoRequestDTO();
        dto.setNombre("");
        dto.setCategoria("");
        dto.setPrecio(-1);
        dto.setStockActual(-5);
        dto.setStockMinimo(-2);

        Set<ConstraintViolation<ProductoRequestDTO>> errores = validar(dto);

        assertFalse(errores.isEmpty());
        assertTrue(errores.size() >= 5);
    }

    @Test
    void productoRequestDTO_conNombreMuyCorto_debeTenerErrorDeValidacion() {
        ProductoRequestDTO dto = new ProductoRequestDTO();
        dto.setNombre("A");
        dto.setCategoria("Vacunas");
        dto.setPrecio(15000);
        dto.setStockActual(20);
        dto.setStockMinimo(5);

        Set<ConstraintViolation<ProductoRequestDTO>> errores = validar(dto);

        assertFalse(errores.isEmpty());
        assertTrue(
                errores.stream()
                        .anyMatch(error -> error.getPropertyPath().toString().equals("nombre"))
        );
    }

    private Set<ConstraintViolation<ProductoRequestDTO>> validar(ProductoRequestDTO dto) {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();
            return validator.validate(dto);
        }
    }
}
