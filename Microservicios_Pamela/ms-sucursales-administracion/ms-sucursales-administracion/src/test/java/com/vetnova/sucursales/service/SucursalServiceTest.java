package com.vetnova.sucursales.service;

import com.vetnova.sucursales.model.Sucursal;
import com.vetnova.sucursales.repository.SucursalRepository;
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
class SucursalServiceTest {

    @Mock
    private SucursalRepository sucursalRepository;

    @InjectMocks
    private SucursalService sucursalService;

    private Sucursal sucursal;

    @BeforeEach
    void setUp() {
        sucursal = new Sucursal();
        sucursal.setIdSucursal(1L);
        sucursal.setNombre("Sucursal Centro");
        sucursal.setDireccion("Av. Principal 123");
        sucursal.setTelefono("56912345678");
        sucursal.setCiudad("Concepción");
        sucursal.setEstado("ACTIVA");
    }

    @Test
    void listarSucursales_debeRetornarLista() {
        when(sucursalRepository.findAll()).thenReturn(List.of(sucursal));

        List<Sucursal> resultado = sucursalService.listarSucursales();

        assertEquals(1, resultado.size());
        verify(sucursalRepository).findAll();
    }

    @Test
    void listarSucursalesActivas_debeRetornarActivas() {
        when(sucursalRepository.findByEstadoIgnoreCase("ACTIVA")).thenReturn(List.of(sucursal));

        List<Sucursal> resultado = sucursalService.listarSucursalesActivas();

        assertEquals(1, resultado.size());
        assertEquals("ACTIVA", resultado.get(0).getEstado());
        verify(sucursalRepository).findByEstadoIgnoreCase("ACTIVA");
    }

    @Test
    void buscarPorId_cuandoExiste_debeRetornarSucursal() {
        when(sucursalRepository.findById(1L)).thenReturn(Optional.of(sucursal));

        Sucursal resultado = sucursalService.buscarPorId(1L);

        assertEquals(1L, resultado.getIdSucursal());
        verify(sucursalRepository).findById(1L);
    }

    @Test
    void buscarPorId_cuandoNoExiste_debeLanzarExcepcion() {
        when(sucursalRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> sucursalService.buscarPorId(99L));

        assertEquals("Sucursal no encontrada con ID: 99", ex.getMessage());
        verify(sucursalRepository).findById(99L);
    }

    @Test
    void crearSucursal_debeGuardarComoActiva() {
        sucursal.setEstado("INACTIVA");
        when(sucursalRepository.save(sucursal)).thenReturn(sucursal);

        Sucursal resultado = sucursalService.crearSucursal(sucursal);

        assertEquals("ACTIVA", resultado.getEstado());
        verify(sucursalRepository).save(sucursal);
    }

    @Test
    void actualizarSucursal_debeModificarDatos() {
        Sucursal datos = new Sucursal();
        datos.setNombre("Sucursal Norte");
        datos.setDireccion("Nueva Dirección");
        datos.setTelefono("56987654321");
        datos.setCiudad("Talcahuano");

        when(sucursalRepository.findById(1L)).thenReturn(Optional.of(sucursal));
        when(sucursalRepository.save(sucursal)).thenReturn(sucursal);

        Sucursal resultado = sucursalService.actualizarSucursal(1L, datos);

        assertEquals("Sucursal Norte", resultado.getNombre());
        assertEquals("Nueva Dirección", resultado.getDireccion());
        assertEquals("56987654321", resultado.getTelefono());
        assertEquals("Talcahuano", resultado.getCiudad());
        verify(sucursalRepository).save(sucursal);
    }

    @Test
    void desactivarSucursal_debeCambiarEstadoAInactiva() {
        when(sucursalRepository.findById(1L)).thenReturn(Optional.of(sucursal));
        when(sucursalRepository.save(sucursal)).thenReturn(sucursal);

        sucursalService.desactivarSucursal(1L);

        assertEquals("INACTIVA", sucursal.getEstado());
        verify(sucursalRepository).save(sucursal);
    }

    @Test
    void buscarPorCiudad_debeRetornarLista() {
        when(sucursalRepository.findByCiudadIgnoreCase("Concepción")).thenReturn(List.of(sucursal));

        List<Sucursal> resultado = sucursalService.buscarPorCiudad("Concepción");

        assertEquals(1, resultado.size());
        verify(sucursalRepository).findByCiudadIgnoreCase("Concepción");
    }
}