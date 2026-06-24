package cl.duoc.gestionmesas.service;

import cl.duoc.gestionmesas.model.Mesa;
import cl.duoc.gestionmesas.repository.MesaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MesaServiceTest {

    @Mock
    private MesaRepository mesaRepository;

    @InjectMocks
    private MesaService mesaService;

    private Mesa mesaEjemplo;

    @BeforeEach
    void setUp() {
        mesaEjemplo = new Mesa(1, 10, 4, "disponible");
    }

    @Test
    void listarMesas_retornaLista() {
        when(mesaRepository.findAll()).thenReturn(Arrays.asList(mesaEjemplo));
        List<Mesa> resultado = mesaService.listarMesas();
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(10, resultado.get(0).getNumero());
    }

    @Test
    void guardarMesa_exitoso() {
        when(mesaRepository.save(any(Mesa.class))).thenReturn(mesaEjemplo);
        Mesa resultado = mesaService.guardarMesa(mesaEjemplo);
        assertNotNull(resultado);
        assertEquals("disponible", resultado.getEstado());
    }

    @Test
    void eliminarMesa_exitoso() {
        when(mesaRepository.existsById(1)).thenReturn(true);
        doNothing().when(mesaRepository).deleteById(1);

        assertDoesNotThrow(() -> mesaService.eliminarMesa(1));
        verify(mesaRepository, times(1)).deleteById(1);
    }

    @Test
    void eliminarMesa_lanzaExcepcion_cuandoNoExiste() {
        when(mesaRepository.existsById(99)).thenReturn(false);
        assertThrows(RuntimeException.class, () -> mesaService.eliminarMesa(99));
    }

    @Test
    void actualizarMesa_exitoso() {
        when(mesaRepository.findById(1)).thenReturn(Optional.of(mesaEjemplo));
        when(mesaRepository.save(any(Mesa.class))).thenReturn(mesaEjemplo);

        Mesa resultado = mesaService.actualizarMesa(1, "ocupada");
        
        assertEquals("ocupada", resultado.getEstado());
        verify(mesaRepository, times(1)).save(mesaEjemplo);
    }

    @Test
    void actualizarMesa_lanzaExcepcion_cuandoEstadoEsInvalido() {
        when(mesaRepository.findById(1)).thenReturn(Optional.of(mesaEjemplo));
        
        assertThrows(IllegalArgumentException.class, () -> mesaService.actualizarMesa(1, "volando"));
    }

    @Test
    void actualizarMesa_lanzaExcepcion_cuandoMesaNoExiste() {
        when(mesaRepository.findById(99)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> mesaService.actualizarMesa(99, "disponible"));
    }

    @Test
    void buscarMesaPorId_retornaMesa_cuandoExiste() {
        when(mesaRepository.findById(1)).thenReturn(Optional.of(mesaEjemplo));
        Mesa resultado = mesaService.buscarMesaPorId(1);
        assertNotNull(resultado);
        assertEquals(1, resultado.getId());
    }

    @Test
    void buscarMesaPorId_lanzaExcepcion_cuandoNoExiste() {
        when(mesaRepository.findById(99)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> mesaService.buscarMesaPorId(99));
    }
}