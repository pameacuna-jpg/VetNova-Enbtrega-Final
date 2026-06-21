package com.vetnova.sucursales.service;

import com.vetnova.sucursales.model.BoxAtencion;
import com.vetnova.sucursales.repository.BoxAtencionRepository;
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
class BoxAtencionServiceTest {

    @Mock
    private BoxAtencionRepository boxAtencionRepository;

    @InjectMocks
    private BoxAtencionService boxAtencionService;

    private BoxAtencion box;

    @BeforeEach
    void setUp() {
        box = new BoxAtencion();
        box.setIdBox(1L);
        box.setNombre("Box 1");
        box.setTipoAtencion("Consulta");
        box.setIdSucursal(1L);
        box.setDisponible(true);
    }

    @Test
    void listarBoxes_debeRetornarLista() {
        when(boxAtencionRepository.findAll()).thenReturn(List.of(box));

        List<BoxAtencion> resultado = boxAtencionService.listarBoxes();

        assertEquals(1, resultado.size());
        verify(boxAtencionRepository).findAll();
    }

    @Test
    void listarDisponibles_debeRetornarDisponibles() {
        when(boxAtencionRepository.findByDisponibleTrue()).thenReturn(List.of(box));

        List<BoxAtencion> resultado = boxAtencionService.listarDisponibles();

        assertEquals(1, resultado.size());
        assertTrue(resultado.get(0).getDisponible());
        verify(boxAtencionRepository).findByDisponibleTrue();
    }

    @Test
    void buscarPorId_cuandoExiste_debeRetornarBox() {
        when(boxAtencionRepository.findById(1L)).thenReturn(Optional.of(box));

        BoxAtencion resultado = boxAtencionService.buscarPorId(1L);

        assertEquals(1L, resultado.getIdBox());
        verify(boxAtencionRepository).findById(1L);
    }

    @Test
    void buscarPorId_cuandoNoExiste_debeLanzarExcepcion() {
        when(boxAtencionRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> boxAtencionService.buscarPorId(99L));

        assertEquals("Box no encontrado con ID: 99", ex.getMessage());
        verify(boxAtencionRepository).findById(99L);
    }

    @Test
    void crearBox_debeGuardarComoDisponible() {
        box.setDisponible(false);
        when(boxAtencionRepository.save(box)).thenReturn(box);

        BoxAtencion resultado = boxAtencionService.crearBox(box);

        assertTrue(resultado.getDisponible());
        verify(boxAtencionRepository).save(box);
    }

    @Test
    void actualizarBox_debeModificarDatos() {
        BoxAtencion datos = new BoxAtencion();
        datos.setNombre("Box 2");
        datos.setTipoAtencion("Cirugía");
        datos.setIdSucursal(2L);

        when(boxAtencionRepository.findById(1L)).thenReturn(Optional.of(box));
        when(boxAtencionRepository.save(box)).thenReturn(box);

        BoxAtencion resultado = boxAtencionService.actualizarBox(1L, datos);

        assertEquals("Box 2", resultado.getNombre());
        assertEquals("Cirugía", resultado.getTipoAtencion());
        assertEquals(2L, resultado.getIdSucursal());
        verify(boxAtencionRepository).save(box);
    }

    @Test
    void desactivarBox_debeCambiarDisponibleFalse() {
        when(boxAtencionRepository.findById(1L)).thenReturn(Optional.of(box));
        when(boxAtencionRepository.save(box)).thenReturn(box);

        boxAtencionService.desactivarBox(1L);

        assertFalse(box.getDisponible());
        verify(boxAtencionRepository).save(box);
    }

    @Test
    void buscarPorSucursal_debeRetornarLista() {
        when(boxAtencionRepository.findByIdSucursal(1L)).thenReturn(List.of(box));

        List<BoxAtencion> resultado = boxAtencionService.buscarPorSucursal(1L);

        assertEquals(1, resultado.size());
        verify(boxAtencionRepository).findByIdSucursal(1L);
    }
}