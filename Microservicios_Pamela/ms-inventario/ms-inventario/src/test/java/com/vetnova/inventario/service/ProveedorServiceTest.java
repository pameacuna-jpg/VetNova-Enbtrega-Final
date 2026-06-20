package com.vetnova.inventario.service;

import com.vetnova.inventario.model.Proveedor;
import com.vetnova.inventario.repository.ProveedorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProveedorServiceTest {

    @Mock
    private ProveedorRepository proveedorRepository;

    @InjectMocks
    private ProveedorService proveedorService;

    private Proveedor proveedor;

    @BeforeEach
    void setUp() {
        proveedor = new Proveedor();
        proveedor.setIdProveedor(1L);
        proveedor.setNombre("Proveedor Test");
        proveedor.setTelefono("56912345678");
        proveedor.setEmail("proveedor@test.cl");
        proveedor.setDireccion("Concepción");
        proveedor.setActivo(true);
    }

    @Test
    void listarProveedores_debeRetornarSoloActivos() {
        when(proveedorRepository.findByActivoTrue())
                .thenReturn(List.of(proveedor));

        List<Proveedor> resultado = proveedorService.listarProveedores();

        assertEquals(1, resultado.size());
        assertTrue(resultado.get(0).getActivo());
        verify(proveedorRepository).findByActivoTrue();
    }

    @Test
    void buscarPorId_cuandoExiste_debeRetornarProveedor() {
        when(proveedorRepository.findById(1L))
                .thenReturn(Optional.of(proveedor));

        Proveedor resultado = proveedorService.buscarPorId(1L);

        assertEquals(1L, resultado.getIdProveedor());
        assertEquals("Proveedor Test", resultado.getNombre());
        verify(proveedorRepository).findById(1L);
    }

    @Test
    void buscarPorId_cuandoNoExiste_debeLanzarExcepcion() {
        when(proveedorRepository.findById(99L))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> proveedorService.buscarPorId(99L)
        );

        assertEquals("Proveedor no encontrado con ID: 99", exception.getMessage());
        verify(proveedorRepository).findById(99L);
    }

    @Test
    void crearProveedor_debeGuardarProveedorActivo() {
        proveedor.setActivo(false);

        when(proveedorRepository.save(proveedor))
                .thenReturn(proveedor);

        Proveedor resultado = proveedorService.crearProveedor(proveedor);

        assertTrue(resultado.getActivo());
        verify(proveedorRepository).save(proveedor);
    }

    @Test
    void actualizarProveedor_debeModificarDatosYGuardar() {
        Proveedor datos = new Proveedor();
        datos.setNombre("Proveedor Actualizado");
        datos.setTelefono("56987654321");
        datos.setEmail("actualizado@test.cl");
        datos.setDireccion("Santiago");

        when(proveedorRepository.findById(1L))
                .thenReturn(Optional.of(proveedor));
        when(proveedorRepository.save(proveedor))
                .thenReturn(proveedor);

        Proveedor resultado = proveedorService.actualizarProveedor(1L, datos);

        assertEquals("Proveedor Actualizado", resultado.getNombre());
        assertEquals("56987654321", resultado.getTelefono());
        assertEquals("actualizado@test.cl", resultado.getEmail());
        assertEquals("Santiago", resultado.getDireccion());

        verify(proveedorRepository).findById(1L);
        verify(proveedorRepository).save(proveedor);
    }

    @Test
    void eliminarProveedor_debeRealizarEliminacionLogica() {
        when(proveedorRepository.findById(1L))
                .thenReturn(Optional.of(proveedor));
        when(proveedorRepository.save(proveedor))
                .thenReturn(proveedor);

        proveedorService.eliminarProveedor(1L);

        assertFalse(proveedor.getActivo());
        verify(proveedorRepository).findById(1L);
        verify(proveedorRepository).save(proveedor);
    }

    @Test
    void buscarPorNombre_debeRetornarListaCoincidente() {
        when(proveedorRepository.findByNombreContainingIgnoreCase("test"))
                .thenReturn(List.of(proveedor));

        List<Proveedor> resultado = proveedorService.buscarPorNombre("test");

        assertEquals(1, resultado.size());
        assertEquals("Proveedor Test", resultado.get(0).getNombre());
        verify(proveedorRepository).findByNombreContainingIgnoreCase("test");
    }
}