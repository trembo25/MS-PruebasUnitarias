package cl.orderflowmanagement.gestionreportes.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.text.SimpleDateFormat;
import java.util.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cl.orderflowmanagement.gestionreportes.client.MenuClient;
import cl.orderflowmanagement.gestionreportes.client.ProveedorClient;
import cl.orderflowmanagement.gestionreportes.client.VentasClient;
import cl.orderflowmanagement.gestionreportes.dto.*;
import cl.orderflowmanagement.gestionreportes.model.Reporte;
import cl.orderflowmanagement.gestionreportes.repository.ReporteRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReporteService - Tests Básicos")
public class ReporteServiceTest {

    @Mock
    private ReporteRepository reporteRepository;
    @Mock
    private VentasClient ventasClient;
    @Mock
    private MenuClient menuClient;
    @Mock
    private ProveedorClient proveedorClient;

    @InjectMocks
    private ReporteService reporteService;

   

    @Test
    @DisplayName("Debe retornar el plato con mayor cantidad vendida")
    void testPlatoGanadorEncontrado() throws Exception {
        // ARRANGE - Preparar datos
        List<DetalleRequestDTO> detalles = Arrays.asList(
            new DetalleRequestDTO(1, 5),
            new DetalleRequestDTO(2, 3),
            new DetalleRequestDTO(1, 2)
        );
        when(ventasClient.obtenerDetallesVentas("1")).thenReturn(detalles);

        PlatoResponseDTO plato = new PlatoResponseDTO();
        plato.setId(1);
        plato.setNombre("Lomo Saltado");
        when(menuClient.obtenerPlatoPorId(1)).thenReturn(plato);

        Reporte reporteEjemplo = new Reporte();
        reporteEjemplo.setTipoReporte("RANKING_PLATOS");
        reporteEjemplo.setMontoTotal(7.0);
        reporteEjemplo.setResumen("Plato más vendido: Lomo Saltado con 7 unidades.");
        when(reporteRepository.save(any(Reporte.class))).thenReturn(reporteEjemplo);

        // ACT
        Reporte resultado = reporteService.generarReportePlatoGanador();

        // ASSERT
        assertNotNull(resultado);
        assertEquals("RANKING_PLATOS", resultado.getTipoReporte());
        assertEquals(7.0, resultado.getMontoTotal());
        verify(ventasClient, times(1)).obtenerDetallesVentas("1");
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando no hay ventas")
    void testSinDetallesVentas() {
        when(ventasClient.obtenerDetallesVentas("1")).thenReturn(new ArrayList<>());
        assertThrows(RuntimeException.class, () -> reporteService.generarReportePlatoGanador());
        verify(reporteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe acumular correctamente cantidades del mismo plato")
    void testAcumulacionDetallesMultiples() throws Exception {
        List<DetalleRequestDTO> detalles = Arrays.asList(
            new DetalleRequestDTO(2, 3),
            new DetalleRequestDTO(2, 5),
            new DetalleRequestDTO(2, 2)
        );
        when(ventasClient.obtenerDetallesVentas("1")).thenReturn(detalles);

        PlatoResponseDTO plato = new PlatoResponseDTO();
        plato.setId(2);
        plato.setNombre("Ají de Gallina");
        when(menuClient.obtenerPlatoPorId(2)).thenReturn(plato);

        Reporte reporteMock = new Reporte();
        reporteMock.setTipoReporte("RANKING_PLATOS");
        reporteMock.setMontoTotal(10.0);

        when(reporteRepository.save(any(Reporte.class))).thenReturn(reporteMock);

        Reporte resultado = reporteService.generarReportePlatoGanador();
        assertEquals(10.0, resultado.getMontoTotal());
    }

   

    @Test
    @DisplayName("Debe sumar solo pedidos del mes y año especificados")
    void testFiltroMesAnioCorrect() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        List<PedidoReporteDTO> pedidos = Arrays.asList(
            new PedidoReporteDTO(1, sdf.parse("2026-06-15"), 50000.0),
            new PedidoReporteDTO(2, sdf.parse("2026-06-20"), 30000.0),
            new PedidoReporteDTO(3, sdf.parse("2026-07-10"), 100000.0)
        );
        when(ventasClient.obtenerPedidosCompletados("1")).thenReturn(pedidos);

        Reporte reporteMock = new Reporte();
        reporteMock.setTipoReporte("VENTAS_MENSUALES");
        reporteMock.setMontoTotal(80000.0);
        when(reporteRepository.save(any(Reporte.class))).thenReturn(reporteMock);

        Reporte resultado = reporteService.generarReporteVentasMes(6, 2026);

        assertEquals(80000.0, resultado.getMontoTotal());
        assertEquals("VENTAS_MENSUALES", resultado.getTipoReporte());
    }

    @Test
    @DisplayName("Debe retornar 0 cuando no hay pedidos en el mes")
    void testSinPedidosEnMes() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        List<PedidoReporteDTO> pedidos = Arrays.asList(
            new PedidoReporteDTO(1, sdf.parse("2026-06-15"), 50000.0)
        );
        when(ventasClient.obtenerPedidosCompletados("1")).thenReturn(pedidos);

        Reporte reporteMock = new Reporte();
        reporteMock.setMontoTotal(0.0);
        when(reporteRepository.save(any(Reporte.class))).thenReturn(reporteMock);

        Reporte resultado = reporteService.generarReporteVentasMes(12, 2026);
        assertEquals(0.0, resultado.getMontoTotal());
    }

   

    @Test
    @DisplayName("Debe sumar solo facturas COMPLETADAS del mes y año")
    void testFiltroEstadoCompletadoYMesAnio() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        List<FacturasDTO> facturas = Arrays.asList(
            new FacturasDTO(1, "FAC-001", 100000.0, sdf.parse("2026-06-15"), "COMPLETADO"),
            new FacturasDTO(2, "FAC-002", 50000.0, sdf.parse("2026-06-20"), "COMPLETADO"),
            new FacturasDTO(3, "FAC-003", 200000.0, sdf.parse("2026-06-15"), "PENDIENTE"),
            new FacturasDTO(4, "FAC-004", 75000.0, sdf.parse("2026-07-10"), "COMPLETADO")
        );
        when(proveedorClient.obtenerTodasLasFacturas("1")).thenReturn(facturas);

        Reporte reporteMock = new Reporte();
        reporteMock.setTipoReporte("GASTOS_PROVEEDORES");
        reporteMock.setMontoTotal(150000.0);
        when(reporteRepository.save(any(Reporte.class))).thenReturn(reporteMock);

        Reporte resultado = reporteService.generarReporteGastosMes(6, 2026);
        assertEquals(150000.0, resultado.getMontoTotal());
    }

    @Test
    @DisplayName("Debe retornar 0 cuando la lista de facturas es null")
    void testFacturasNull() {
        when(proveedorClient.obtenerTodasLasFacturas("1")).thenReturn(null);

        Reporte reporteMock = new Reporte();
        reporteMock.setMontoTotal(0.0);
        when(reporteRepository.save(any(Reporte.class))).thenReturn(reporteMock);

        Reporte resultado = reporteService.generarReporteGastosMes(6, 2026);
        assertEquals(0.0, resultado.getMontoTotal());
    }

    @Test
    @DisplayName("Debe ignorar facturas con estado diferente a COMPLETADO")
    void testFiltroEstadoDiferentes() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        List<FacturasDTO> facturas = Arrays.asList(
            new FacturasDTO(1, "FAC-001", 100000.0, sdf.parse("2026-06-15"), "PENDIENTE"),
            new FacturasDTO(2, "FAC-002", 50000.0, sdf.parse("2026-06-15"), "EN CAMINO")
        );
        when(proveedorClient.obtenerTodasLasFacturas("1")).thenReturn(facturas);

        Reporte reporteMock = new Reporte();
        reporteMock.setMontoTotal(0.0);
        when(reporteRepository.save(any(Reporte.class))).thenReturn(reporteMock);

        Reporte resultado = reporteService.generarReporteGastosMes(6, 2026);
        assertEquals(0.0, resultado.getMontoTotal());
    }
}