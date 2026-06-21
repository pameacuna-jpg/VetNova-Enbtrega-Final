package com.vetnova.sucursales.service;

import com.vetnova.sucursales.model.HorarioSucursal;
import com.vetnova.sucursales.repository.HorarioSucursalRepository;
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
class HorarioSucursalServiceTest {

    @Mock
    private HorarioSucursalRepository horarioSucursalRepository;

    @InjectMocks
    private HorarioSucursalService horarioSucursalService;

    private HorarioSucursal horario;

    @BeforeEach
    void setUp() {
        horario = new HorarioSucursal();
        horario.setIdHorario(1L);
        horario.setDia("Lunes");
        horario.setHoraApertura("09:00");
        horario.setHoraCierre("18:00");
        horario.setIdSucursal(1L);
        horario.setActivo(true);
    }

    @Test
    void listarHorarios_debeRetornarLista() {
        when(horarioSucursalRepository.findAll()).thenReturn(List.of(horario));

        List<HorarioSucursal> resultado = horarioSucursalService.listarHorarios();

        assertEquals(1, resultado.size());
        verify(horarioSucursalRepository).findAll();
    }

    @Test
    void listarActivos_debeRetornarActivos() {
        when(horarioSucursalRepository.findByActivoTrue()).thenReturn(List.of(horario));

        List<HorarioSucursal> resultado = horarioSucursalService.listarActivos();

        assertEquals(1, resultado.size());
        assertTrue(resultado.get(0).getActivo());
        verify(horarioSucursalRepository).findByActivoTrue();
    }

    @Test
    void buscarPorId_cuandoExiste_debeRetornarHorario() {
        when(horarioSucursalRepository.findById(1L)).thenReturn(Optional.of(horario));

        HorarioSucursal resultado = horarioSucursalService.buscarPorId(1L);

        assertEquals(1L, resultado.getIdHorario());
        verify(horarioSucursalRepository).findById(1L);
    }

    @Test
    void buscarPorId_cuandoNoExiste_debeLanzarExcepcion() {
        when(horarioSucursalRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> horarioSucursalService.buscarPorId(99L));

        assertEquals("Horario no encontrado con ID: 99", ex.getMessage());
        verify(horarioSucursalRepository).findById(99L);
    }

    @Test
    void crearHorario_debeGuardarComoActivo() {
        horario.setActivo(false);
        when(horarioSucursalRepository.save(horario)).thenReturn(horario);

        HorarioSucursal resultado = horarioSucursalService.crearHorario(horario);

        assertTrue(resultado.getActivo());
        verify(horarioSucursalRepository).save(horario);
    }

    @Test
    void actualizarHorario_debeModificarDatos() {
        HorarioSucursal datos = new HorarioSucursal();
        datos.setDia("Martes");
        datos.setHoraApertura("10:00");
        datos.setHoraCierre("19:00");
        datos.setIdSucursal(2L);

        when(horarioSucursalRepository.findById(1L)).thenReturn(Optional.of(horario));
        when(horarioSucursalRepository.save(horario)).thenReturn(horario);

        HorarioSucursal resultado = horarioSucursalService.actualizarHorario(1L, datos);

        assertEquals("Martes", resultado.getDia());
        assertEquals("10:00", resultado.getHoraApertura());
        assertEquals("19:00", resultado.getHoraCierre());
        assertEquals(2L, resultado.getIdSucursal());
        verify(horarioSucursalRepository).save(horario);
    }

    @Test
    void desactivarHorario_debeCambiarActivoFalse() {
        when(horarioSucursalRepository.findById(1L)).thenReturn(Optional.of(horario));
        when(horarioSucursalRepository.save(horario)).thenReturn(horario);

        horarioSucursalService.desactivarHorario(1L);

        assertFalse(horario.getActivo());
        verify(horarioSucursalRepository).save(horario);
    }

    @Test
    void buscarPorSucursal_debeRetornarLista() {
        when(horarioSucursalRepository.findByIdSucursal(1L)).thenReturn(List.of(horario));

        List<HorarioSucursal> resultado = horarioSucursalService.buscarPorSucursal(1L);

        assertEquals(1, resultado.size());
        verify(horarioSucursalRepository).findByIdSucursal(1L);
    }
}
