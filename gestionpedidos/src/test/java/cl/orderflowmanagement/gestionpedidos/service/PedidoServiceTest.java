package cl.orderflowmanagement.gestionpedidos.service;

import cl.orderflowmanagement.gestionpedidos.client.*;
import cl.orderflowmanagement.gestionpedidos.dto.*;
import cl.orderflowmanagement.gestionpedidos.model.DetallePedido;
import cl.orderflowmanagement.gestionpedidos.model.Pedido;
import cl.orderflowmanagement.gestionpedidos.repository.PedidoRepository;
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
public class PedidoServiceTest {

    @Mock private PedidoRepository pedidoRepository;
    @Mock private EmpleadoClient empleadoClient;
    @Mock private MesaClient mesaClient;
    @Mock private MenuClient menuClient;
    @Mock private CocinaClient cocinaClient;
    @Mock private FacturacionClient facturacionClient;

    @InjectMocks
    private PedidoService pedidoService;

    private Pedido pedidoEjemplo;
    private DetallePedido detalleEjemplo;
    private EmpleadoResponseDTO garzonValido;
    private MesaResponseDTO mesaDisponible;
    private PlatoResponseDTO platoDisponible;

    @BeforeEach
    void setUp() {
        // Pedido base con 1 detalle
        pedidoEjemplo = new Pedido(1, "pendiente", new Date(), 12000.0, 1, 5, new ArrayList<>());
        detalleEjemplo = new DetallePedido(10, 2, 12000.0, 15, pedidoEjemplo);
        pedidoEjemplo.getDetalles().add(detalleEjemplo);

        RolEmpleadoDTO rolGarzon = new RolEmpleadoDTO(2, "garzon");
        garzonValido = new EmpleadoResponseDTO();
        garzonValido.setId(1);
        garzonValido.setRolEmpleado(rolGarzon);

        mesaDisponible = new MesaResponseDTO();
        mesaDisponible.setId(5);
        mesaDisponible.setEstado("disponible");

        platoDisponible = new PlatoResponseDTO();
        platoDisponible.setId(15);
        platoDisponible.setNombre("Hamburguesa");
        platoDisponible.setPrecio(6000.0);
        platoDisponible.setDisponible(true);
    }

    // ==========================================
    // TESTS PARA LISTAR
    // ==========================================
    @Test
    void getPedidos_retornaLista() {
        when(pedidoRepository.findAll()).thenReturn(Arrays.asList(pedidoEjemplo));
        List<Pedido> resultado = pedidoService.getPedidos();
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
    }

    // ==========================================
    // TESTS PARA CREAR PEDIDO
    // ==========================================
    @Test
    void createPedido_exitoso() {
        when(empleadoClient.obtenerEmpleadoPorId(1)).thenReturn(garzonValido);
        when(mesaClient.obtenerMesaPorId(5)).thenReturn(mesaDisponible);
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoEjemplo);

        Pedido resultado = pedidoService.createPedido(5, 1);

        assertNotNull(resultado);
        verify(mesaClient, times(1)).actualizarEstadoMesa(eq(5), anyMap());
    }

    @Test
    void createPedido_retornaNull_cuandoGarzonNoTienePermisos() {
        garzonValido.getRolEmpleado().setId(3); // Rol inválido
        when(empleadoClient.obtenerEmpleadoPorId(1)).thenReturn(garzonValido);
        Pedido resultado = pedidoService.createPedido(5, 1);
        assertNull(resultado);
    }

    @Test
    void createPedido_retornaNull_cuandoMesaNoDisponible() {
        mesaDisponible.setEstado("ocupada");
        when(empleadoClient.obtenerEmpleadoPorId(1)).thenReturn(garzonValido);
        when(mesaClient.obtenerMesaPorId(5)).thenReturn(mesaDisponible);
        Pedido resultado = pedidoService.createPedido(5, 1);
        assertNull(resultado);
    }

    @Test
    void createPedido_manejaErrorDeMesaClient_silenciosamente() {
        when(empleadoClient.obtenerEmpleadoPorId(1)).thenReturn(garzonValido);
        when(mesaClient.obtenerMesaPorId(5)).thenReturn(mesaDisponible);
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoEjemplo);
        doThrow(new RuntimeException("Error Mesa")).when(mesaClient).actualizarEstadoMesa(eq(5), anyMap());

        Pedido resultado = pedidoService.createPedido(5, 1);
        assertNotNull(resultado); // Se crea igual aunque la mesa falle
    }

    // ==========================================
    // TESTS PARA AÑADIR PRODUCTO (COCINA)
    // ==========================================
    @Test
    void addProducto_exitoso() {
        when(pedidoRepository.findById(1)).thenReturn(Optional.of(pedidoEjemplo));
        when(menuClient.obtenerPlatoPorId(15)).thenReturn(platoDisponible);
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoEjemplo);
        when(cocinaClient.enviarOrden(anyString(), any(OrdenCocinaDTO.class))).thenReturn(new Object()); // FIX DEL OBJECT

        Pedido resultado = pedidoService.addProducto(1, 15, 2);

        assertNotNull(resultado);
        verify(cocinaClient, times(1)).enviarOrden(anyString(), any(OrdenCocinaDTO.class));
    }

    @Test
    void addProducto_retornaNull_cuandoPlatoNoDisponible() {
        platoDisponible.setDisponible(false);
        when(pedidoRepository.findById(1)).thenReturn(Optional.of(pedidoEjemplo));
        when(menuClient.obtenerPlatoPorId(15)).thenReturn(platoDisponible);

        Pedido resultado = pedidoService.addProducto(1, 15, 2);
        assertNull(resultado);
    }

    @Test
    void addProducto_retornaNull_cuandoPedidoYaEstaPagado() {
        pedidoEjemplo.setEstado("pagado");
        when(pedidoRepository.findById(1)).thenReturn(Optional.of(pedidoEjemplo));

        Pedido resultado = pedidoService.addProducto(1, 15, 2);
        assertNull(resultado);
    }

    // ==========================================
    // TESTS PARA ELIMINAR DETALLE
    // ==========================================
    @Test
    void deleteProducto_exitoso() {
        when(pedidoRepository.findById(1)).thenReturn(Optional.of(pedidoEjemplo));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoEjemplo);

        Pedido resultado = pedidoService.deleteProducto(1, 10);

        assertNotNull(resultado);
        assertEquals(0.0, pedidoEjemplo.getTotal()); // 12000 - 12000 = 0
        assertTrue(pedidoEjemplo.getDetalles().isEmpty());
    }

    @Test
    void deleteProducto_retornaNull_cuandoPedidoEsPagado() {
        pedidoEjemplo.setEstado("pagado");
        when(pedidoRepository.findById(1)).thenReturn(Optional.of(pedidoEjemplo));
        Pedido resultado = pedidoService.deleteProducto(1, 10);
        assertNull(resultado);
    }

    // ==========================================
    // TESTS PARA ACTUALIZAR CANTIDAD
    // ==========================================
    @Test
    void updateCantidad_exitoso() {
        // La hamburguesa costaba 6000. Pediremos 3 (18000). El total era 12000.
        // Se restan 12000 (0) y se suman 18000. Total esperado: 18000.
        when(pedidoRepository.findById(1)).thenReturn(Optional.of(pedidoEjemplo));
        when(menuClient.obtenerPlatoPorId(15)).thenReturn(platoDisponible);
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoEjemplo);

        Pedido resultado = pedidoService.updateCantidad(1, 10, 3);

        assertNotNull(resultado);
        assertEquals(18000.0, pedidoEjemplo.getTotal());
        assertEquals(3, detalleEjemplo.getCantidad());
    }

    @Test
    void updateCantidad_retornaNull_cuandoPlatoFalla() {
        when(pedidoRepository.findById(1)).thenReturn(Optional.of(pedidoEjemplo));
        when(menuClient.obtenerPlatoPorId(15)).thenThrow(new RuntimeException("Error menu"));

        Pedido resultado = pedidoService.updateCantidad(1, 10, 3);
        assertNull(resultado);
    }

    // ==========================================
    // TESTS PARA PAGAR (FACTURACIÓN)
    // ==========================================
    @Test
    void payPedido_exitoso() {
        PagoResponseDTO boletaAprobada = new PagoResponseDTO("aprobado", 1234, 12000.0);

        when(pedidoRepository.findById(1)).thenReturn(Optional.of(pedidoEjemplo));
        when(facturacionClient.enviarPago(any(PagoRequestDTO.class))).thenReturn(boletaAprobada);
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoEjemplo);

        Pedido resultado = pedidoService.payPedido(1, "debito");

        assertNotNull(resultado);
        assertEquals("pagado", resultado.getEstado());
        verify(mesaClient, times(1)).actualizarEstadoMesa(eq(5), anyMap());
    }

    @Test
    void payPedido_retornaNull_cuandoPagoEsRechazado() {
        PagoResponseDTO boletaRechazada = new PagoResponseDTO("rechazado", 0, 0.0);
        when(pedidoRepository.findById(1)).thenReturn(Optional.of(pedidoEjemplo));
        when(facturacionClient.enviarPago(any(PagoRequestDTO.class))).thenReturn(boletaRechazada);

        Pedido resultado = pedidoService.payPedido(1, "debito");
        assertNull(resultado);
        assertNotEquals("pagado", pedidoEjemplo.getEstado());
    }

    // ==========================================
    // TESTS PARA REPORTES
    // ==========================================
    @Test
    void getPedidosPagadosParaReporte_retornaExitoso() {
        pedidoEjemplo.setEstado("pagado");
        when(pedidoRepository.findAll()).thenReturn(Arrays.asList(pedidoEjemplo));

        List<PedidoReporteDTO> reportes = pedidoService.getPedidosPagadosParaReporte();
        
        assertEquals(1, reportes.size());
        assertEquals(12000.0, reportes.get(0).getTotal());
    }

    @Test
    void obtenerTodosLosDetallesParaReporte_retornaExitoso() {
        pedidoEjemplo.setEstado("pagado");
        when(pedidoRepository.findAll()).thenReturn(Arrays.asList(pedidoEjemplo));

        List<DetalleRequestDTO> reportes = pedidoService.obtenerTodosLosDetallesParaReporte();
        
        assertEquals(1, reportes.size());
        assertEquals(15, reportes.get(0).getProductoId());
        assertEquals(2, reportes.get(0).getCantidad());
    }
}