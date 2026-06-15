package com.vetnova.mascotas.service;

import com.vetnova.mascotas.dto.MascotaRequestDTO;
import com.vetnova.mascotas.dto.MascotaResponseDTO;
import com.vetnova.mascotas.dto.TransferenciaRequestDTO;
import com.vetnova.mascotas.exception.ResourceNotFoundException;
import com.vetnova.mascotas.model.Especie;
import com.vetnova.mascotas.model.HistorialMascota;
import com.vetnova.mascotas.model.Mascota;
import com.vetnova.mascotas.model.TransferenciaPropietario;
import com.vetnova.mascotas.repository.EspecieRepository;
import com.vetnova.mascotas.repository.MascotaRepository;
import com.vetnova.mascotas.repository.TransferenciaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MascotaServiceImplTest {

    @Mock
    private MascotaRepository mascotaRepository;

    @Mock
    private EspecieRepository especieRepository;

    @Mock
    private TransferenciaRepository transferenciaRepository;

    @InjectMocks
    private MascotaServiceImpl mascotaService;

    private Especie especieCanina;
    private MascotaRequestDTO request;

    @BeforeEach
    void setUp() {
        especieCanina = Especie.builder()
                .idEspecie(1L)
                .nombre("Canino")
                .descripcion("Perro")
                .build();

        request = MascotaRequestDTO.builder()
                .nombre("Firulais")
                .especieNombre("Canino")
                .raza("Labrador")
                .sexo("Macho")
                .fechaNacimiento(LocalDate.now().minusYears(2).minusMonths(1))
                .idCliente(50L)
                .numeroMicrochip("CHIP-001")
                .ultimoPeso(12.5)
                .estaEsterilizado(true)
                .alergiasCriticas("Ninguna")
                .build();
    }

    @Test
    void registrar_cuandoEspecieExiste_debeCrearMascotaActivaConHistorialClinico() {
        // Given
        when(especieRepository.findByNombreIgnoreCase("Canino")).thenReturn(Optional.of(especieCanina));
        when(mascotaRepository.count()).thenReturn(4L);
        when(mascotaRepository.save(any(Mascota.class))).thenAnswer(invocation -> {
            Mascota mascota = invocation.getArgument(0);
            mascota.setIdMascota(20L);
            return mascota;
        });

        // When
        MascotaResponseDTO respuesta = mascotaService.registrar(request);

        // Then
        assertNotNull(respuesta);
        assertEquals(20L, respuesta.getIdMascota());
        assertEquals("Firulais", respuesta.getNombre());
        assertEquals("Canino", respuesta.getEspecie());
        assertEquals("ACTIVO", respuesta.getEstado());
        assertEquals("VHC-00005", respuesta.getNumeroHistoriaClinica());
        assertEquals(12.5, respuesta.getUltimoPeso());
        assertTrue(respuesta.getEstaEsterilizado());
        assertEquals("Ninguna", respuesta.getAlergiasCriticas());

        ArgumentCaptor<Mascota> captor = ArgumentCaptor.forClass(Mascota.class);
        verify(mascotaRepository).save(captor.capture());
        Mascota mascotaGuardada = captor.getValue();
        assertSame(mascotaGuardada, mascotaGuardada.getHistorialMascota().getMascota());
        assertEquals("Apertura de expediente clínico.", mascotaGuardada.getHistorialMascota().getResumenClinico());
    }

    @Test
    void registrar_cuandoEspecieNoExiste_debeLanzarResourceNotFoundException() {
        // Given
        when(especieRepository.findByNombreIgnoreCase("Canino")).thenReturn(Optional.empty());

        // When / Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> mascotaService.registrar(request));

        assertEquals("La Especie especificada no existe en el catálogo maestro", exception.getMessage());
        verify(mascotaRepository, never()).save(any());
    }

    @Test
    void actualizar_cuandoMascotaYEspecieExisten_debeActualizarDatosEHistorial() {
        // Given
        Mascota mascota = crearMascotaBase();
        MascotaRequestDTO actualizacion = MascotaRequestDTO.builder()
                .nombre("Max")
                .especieNombre("Canino")
                .raza("Poodle")
                .sexo("Macho")
                .fechaNacimiento(LocalDate.now().minusYears(3))
                .idCliente(50L)
                .numeroMicrochip("CHIP-999")
                .ultimoPeso(9.7)
                .estaEsterilizado(false)
                .alergiasCriticas("Polen")
                .build();

        when(mascotaRepository.findById(20L)).thenReturn(Optional.of(mascota));
        when(especieRepository.findByNombreIgnoreCase("Canino")).thenReturn(Optional.of(especieCanina));
        when(mascotaRepository.save(any(Mascota.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        MascotaResponseDTO respuesta = mascotaService.actualizar(20L, actualizacion);

        // Then
        assertEquals("Max", respuesta.getNombre());
        assertEquals("Poodle", respuesta.getRaza());
        assertEquals(9.7, respuesta.getUltimoPeso());
        assertFalse(respuesta.getEstaEsterilizado());
        assertEquals("Polen", respuesta.getAlergiasCriticas());
        verify(mascotaRepository).save(mascota);
    }

    @Test
    void obtenerPorId_cuandoMascotaExiste_debeRetornarDetalle() {
        // Given
        Mascota mascota = crearMascotaBase();
        when(mascotaRepository.findById(20L)).thenReturn(Optional.of(mascota));

        // When
        MascotaResponseDTO respuesta = mascotaService.obtenerPorId(20L);

        // Then
        assertEquals(20L, respuesta.getIdMascota());
        assertEquals("Firulais", respuesta.getNombre());
        assertEquals("Canino", respuesta.getEspecie());
        assertTrue(respuesta.getEdadCalculada().contains("Años"));
    }

    @Test
    void obtenerPorId_cuandoMascotaNoExiste_debeLanzarResourceNotFoundException() {
        // Given
        when(mascotaRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> mascotaService.obtenerPorId(99L));

        assertEquals("Paciente no encontrado", exception.getMessage());
    }

    @Test
    void buscarPacientes_debeRetornarPaginaMapeada() {
        // Given
        Mascota mascota = crearMascotaBase();
        Pageable pageable = PageRequest.of(0, 10);
        when(mascotaRepository.buscarPacientesGlobal("fir", pageable))
                .thenReturn(new PageImpl<>(List.of(mascota), pageable, 1));

        // When
        Page<MascotaResponseDTO> resultado = mascotaService.buscarPacientes("fir", pageable);

        // Then
        assertEquals(1, resultado.getTotalElements());
        assertEquals("Firulais", resultado.getContent().get(0).getNombre());
        verify(mascotaRepository).buscarPacientesGlobal("fir", pageable);
    }

    @Test
    void transferirPropietario_cuandoMascotaExiste_debeRegistrarTransferenciaYCambiarCliente() {
        // Given
        Mascota mascota = crearMascotaBase();
        TransferenciaRequestDTO transferencia = TransferenciaRequestDTO.builder()
                .idNuevoCliente(99L)
                .build();

        when(mascotaRepository.findById(20L)).thenReturn(Optional.of(mascota));

        // When
        mascotaService.transferirPropietario(20L, transferencia);

        // Then
        assertEquals(99L, mascota.getIdCliente());

        ArgumentCaptor<TransferenciaPropietario> captor = ArgumentCaptor.forClass(TransferenciaPropietario.class);
        verify(transferenciaRepository).save(captor.capture());
        TransferenciaPropietario auditoria = captor.getValue();
        assertEquals(20L, auditoria.getIdMascota());
        assertEquals(50L, auditoria.getIdClienteAnterior());
        assertEquals(99L, auditoria.getIdClienteNuevo());
        assertNotNull(auditoria.getFechaTransferencia());
        verify(mascotaRepository).save(mascota);
    }

    @Test
    void actualizarEstadoVital_cuandoMascotaExiste_debeGuardarEstadoEnMayusculas() {
        // Given
        Mascota mascota = crearMascotaBase();
        when(mascotaRepository.findById(20L)).thenReturn(Optional.of(mascota));

        // When
        mascotaService.actualizarEstadoVital(20L, "fallecido");

        // Then
        assertEquals("FALLECIDO", mascota.getEstado());
        verify(mascotaRepository).save(mascota);
    }

    private Mascota crearMascotaBase() {
        Mascota mascota = Mascota.builder()
                .idMascota(20L)
                .nombre("Firulais")
                .especie(especieCanina)
                .raza("Labrador")
                .sexo("Macho")
                .fechaNacimiento(LocalDate.now().minusYears(2))
                .idCliente(50L)
                .estado("ACTIVO")
                .numeroMicrochip("CHIP-001")
                .build();

        HistorialMascota historial = HistorialMascota.builder()
                .idHistorial(30L)
                .numeroHistoriaClinica("VHC-00005")
                .resumenClinico("Apertura de expediente clínico.")
                .ultimoPeso(12.5)
                .estaEsterilizado(true)
                .alergiasCriticas("Ninguna")
                .fechaUltimaAtencion(LocalDate.now())
                .build();

        mascota.asignarHistorial(historial);
        return mascota;
    }
}
