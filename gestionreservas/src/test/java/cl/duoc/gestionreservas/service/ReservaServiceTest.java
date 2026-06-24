package cl.duoc.gestionreservas.service;

import cl.duoc.gestionreservas.client.EmpleadoClient;
import cl.duoc.gestionreservas.client.MesaClient;
import cl.duoc.gestionreservas.dto.EmpleadoResponseDTO;
import cl.duoc.gestionreservas.dto.MesaResponseDTO;
import cl.duoc.gestionreservas.model.Reserva;
import cl.duoc.gestionreservas.repository.ReservaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservaServiceTest {

    @Mock private ReservaRepository reservaRepository;
    @Mock private EmpleadoClient empleadoClient;
    @Mock private MesaClient mesaClient;

    @InjectMocks
    private ReservaService reservaService;

    private Reserva reservaEjemplo;
    private MesaResponseDTO mesaDTEjemplo;

    @BeforeEach
    void setUp() {
        reservaEjemplo = new Reserva(1, "Juan", "Pérez", "12.345.675-2", 4, new Date(), 1, 2);
        mesaDTEjemplo = new MesaResponseDTO();
        mesaDTEjemplo.setId(1);
        mesaDTEjemplo.setEstado("disponible");
        mesaDTEjemplo.setCapacidad(4);
    }

    // --- TESTS ORIGINALES ---
    @Test
    void listarReservas_retornaLista() {
        when(reservaRepository.findAll()).thenReturn(Arrays.asList(reservaEjemplo));
        List<Reserva> resultado = reservaService.listarReservas();
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
    }

    @Test
    void guardarReserva_exitoso() {
        when(empleadoClient.obtenerEmpleadoPorId(anyInt())).thenReturn(new EmpleadoResponseDTO());
        when(mesaClient.obtenerMesaPorId(anyInt())).thenReturn(mesaDTEjemplo);
        when(reservaRepository.save(any(Reserva.class))).thenReturn(reservaEjemplo);
        reservaService.guardarReserva(reservaEjemplo);
        verify(mesaClient).actualizarEstadoMesa(eq(1), anyMap());
    }

    @Test
    void guardarReserva_lanzaExcepcion_cuandoEmpleadoNoExiste() {
        when(empleadoClient.obtenerEmpleadoPorId(anyInt())).thenReturn(null);
        assertThrows(IllegalArgumentException.class, () -> reservaService.guardarReserva(reservaEjemplo));
    }

    @Test
    void guardarReserva_lanzaExcepcion_cuandoMesaNoTieneCapacidad() {
        MesaResponseDTO mesaPequeña = new MesaResponseDTO();
        mesaPequeña.setId(1); mesaPequeña.setEstado("disponible"); mesaPequeña.setCapacidad(1);
        when(empleadoClient.obtenerEmpleadoPorId(anyInt())).thenReturn(new EmpleadoResponseDTO());
        when(mesaClient.obtenerMesaPorId(anyInt())).thenReturn(mesaPequeña);
        assertThrows(IllegalArgumentException.class, () -> reservaService.guardarReserva(reservaEjemplo));
    }

    @Test
    void buscarPorId_retornaReserva_cuandoExiste() {
        when(reservaRepository.findById(1)).thenReturn(Optional.of(reservaEjemplo));
        Reserva resultado = reservaService.buscarPorId(1);
        assertNotNull(resultado);
    }

    @Test
    void buscarPorId_lanzaExcepcion_cuandoNoExiste() {
        when(reservaRepository.findById(99)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> reservaService.buscarPorId(99));
    }

    @Test
    void eliminarReserva_exitoso() {
        when(reservaRepository.findById(1)).thenReturn(Optional.of(reservaEjemplo));
        reservaService.eliminarReserva(1);
        verify(reservaRepository).deleteById(1);
        verify(mesaClient).actualizarEstadoMesa(eq(1), anyMap());
    }

    // --- TESTS NUEVOS DE ACTUALIZACIÓN ---
    @Test
    void actualizarReserva_cambioDeMesaExitoso() {
        Reserva reservaNueva = new Reserva(1, "Juan", "Pérez", "12.345.675-2", 4, new Date(), 2, 2);
        when(reservaRepository.findById(1)).thenReturn(Optional.of(reservaEjemplo));
        when(mesaClient.obtenerMesaPorId(2)).thenReturn(mesaDTEjemplo);
        when(reservaRepository.save(any(Reserva.class))).thenReturn(reservaNueva);

        reservaService.actualizarReserva(reservaNueva);

        verify(mesaClient).actualizarEstadoMesa(eq(1), anyMap()); // Libera antigua
        verify(mesaClient).actualizarEstadoMesa(eq(2), anyMap()); // Reserva nueva
    }

    @Test
    void actualizarReserva_lanzaError_cuandoMesaNoDisponible() {
        Reserva reservaNueva = new Reserva(1, "Juan", "Pérez", "12.345.675-2", 4, new Date(), 2, 2);
        MesaResponseDTO mesaOcupada = new MesaResponseDTO();
        mesaOcupada.setEstado("ocupada");

        when(reservaRepository.findById(1)).thenReturn(Optional.of(reservaEjemplo));
        when(mesaClient.obtenerMesaPorId(2)).thenReturn(mesaOcupada);

        assertThrows(IllegalArgumentException.class, () -> reservaService.actualizarReserva(reservaNueva));
    }
}