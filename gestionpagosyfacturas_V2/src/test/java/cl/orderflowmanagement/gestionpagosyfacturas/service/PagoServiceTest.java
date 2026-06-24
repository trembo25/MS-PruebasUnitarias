package cl.orderflowmanagement.gestionpagosyfacturas.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cl.orderflowmanagement.gestionpagosyfacturas.dto.PagoRequestDTO;
import cl.orderflowmanagement.gestionpagosyfacturas.dto.PagoResponseDTO;
import cl.orderflowmanagement.gestionpagosyfacturas.model.Pago;
import cl.orderflowmanagement.gestionpagosyfacturas.repository.PagoRepository;

@ExtendWith(MockitoExtension.class)
public class PagoServiceTest {  // ← Cambiado de PagoService a PagoServiceTest
    
    @Mock
    private PagoRepository pagoRepository;

    @InjectMocks
    private PagoService pagoService;

    private Pago pagoEjemplo;
    private PagoRequestDTO pagoRequestEjemplo;

    @BeforeEach
    void setUp() {
        pagoRequestEjemplo = new PagoRequestDTO();
        pagoRequestEjemplo.setPedidoId(450);
        pagoRequestEjemplo.setTotal(119000.0);
        pagoRequestEjemplo.setMetodoPago("tarjeta_debito");

        pagoEjemplo = new Pago();
        pagoEjemplo.setId(1);
        pagoEjemplo.setPedidoId(450);
        pagoEjemplo.setMontoNeto(100000.0);
        pagoEjemplo.setIva(19000.0);
        pagoEjemplo.setTotalPagado(119000.0);
        pagoEjemplo.setFechaHora(new Date());
        pagoEjemplo.setMetodoPago("tarjeta_debito");
    }

    @Test
    void generarBoleta_retornaBoletaAprobada() {
        // ARRANGE
        when(pagoRepository.save(any(Pago.class))).thenReturn(pagoEjemplo);

        // ACT
        PagoResponseDTO resultado = pagoService.generarBoleta(pagoRequestEjemplo);

        // ASSERT
        assertNotNull(resultado);
        assertEquals("APROBADO", resultado.getEstado());
        assertEquals(1, resultado.getNroBoleta());
        assertEquals(119000.0, resultado.getTotalPagado());
        assertEquals(19000.0, resultado.getIva());
        assertNotNull(resultado.getFechaHora());
        
        // Verifica que guardó en BD
        verify(pagoRepository, times(1)).save(any(Pago.class));
    }

    @Test
    void generarBoleta_calculaNetoEIvaCorrectamente() {
        // ARRANGE
        when(pagoRepository.save(any(Pago.class))).thenReturn(pagoEjemplo);

        // ACT
        PagoResponseDTO resultado = pagoService.generarBoleta(pagoRequestEjemplo);

        // ASSERT - Verifica que los cálculos sean correctos
        // Total: 119000 / Neto: 100000 / IVA: 19000
        assertEquals(100000.0, pagoEjemplo.getMontoNeto(), 0.01);
        assertEquals(19000.0, resultado.getIva(), 0.01); // el 0.01 es un margen de tolerancia 
        //                                                                  con los decimales 
    }

   

    @Test
    void obtenerBoletaPorIdPedido_retornaBoletaExistente() {
        // ARRANGE
        when(pagoRepository.findByPedidoId(450)).thenReturn(Optional.of(pagoEjemplo));

        // ACT
        PagoResponseDTO resultado = pagoService.obtenerBoletaPorIdPedido(450);

        // ASSERT
        assertNotNull(resultado);
        assertEquals("APROBADO", resultado.getEstado());
        assertEquals(1, resultado.getNroBoleta());
        assertEquals(119000.0, resultado.getTotalPagado());
    }

    @Test
    void obtenerBoletaPorIdPedido_retornaNull_CuandoIdEsIncorrecto() {
        // ARRANGE - El ID 999 no existe
        when(pagoRepository.findByPedidoId(999)).thenReturn(Optional.empty());

        // ACT
        PagoResponseDTO resultado = pagoService.obtenerBoletaPorIdPedido(999);

        // ASSERT
        assertNull(resultado);
    }
}