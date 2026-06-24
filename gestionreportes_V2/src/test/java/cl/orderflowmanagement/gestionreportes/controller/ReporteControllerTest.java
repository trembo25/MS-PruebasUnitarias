package cl.orderflowmanagement.gestionreportes.controller;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.Date;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import cl.orderflowmanagement.gestionreportes.model.Reporte;
import cl.orderflowmanagement.gestionreportes.service.ReporteService;

@WebMvcTest(ReporteController.class)

public class ReporteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReporteService reporteService;

    // ===================== TEST 1: PLATO RANKING =====================

    @Test
    @DisplayName("POST /generar/platoranking - Debe retornar 201 con Rol 1")
    void testGenerarPlatoRankingConRol1() throws Exception {
        // ARRANGE
        Reporte reporteEjemplo = new Reporte();
        reporteEjemplo.setTipoReporte("RANKING_PLATOS");
        reporteEjemplo.setResumen("Plato más vendido: Lomo Saltado con 7 unidades");
        reporteEjemplo.setMontoTotal(7.0);
        reporteEjemplo.setFechaGeneracion(new Date());
        
        when(reporteService.generarReportePlatoGanador()).thenReturn(reporteEjemplo);

        // ACT & ASSERT
        mockMvc.perform(post("/v1/orderflow/gestionreportes/generar/platoranking")
                .header("X-Rol-Empleado", "1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.tipoReporte").value("RANKING_PLATOS"));
    }

    @Test
    @DisplayName("POST /generar/platoranking - Debe retornar 403 sin Rol 1")
    void testGenerarPlatoRankingSinRol1() throws Exception {
        mockMvc.perform(post("/v1/orderflow/gestionreportes/generar/platoranking")
                .header("X-Rol-Empleado", "2")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /generar/platoranking - Debe retornar 400 si el servicio falla")
    void testGenerarPlatoRankingConError() throws Exception {
        when(reporteService.generarReportePlatoGanador())
            .thenThrow(new RuntimeException("No hay ventas registradas"));

        mockMvc.perform(post("/v1/orderflow/gestionreportes/generar/platoranking")
                .header("X-Rol-Empleado", "1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

  

    @Test
    @DisplayName("POST /generar/ventas-mes/{mes}/{anio} - Debe retornar 201 con Rol 1")
    void testGenerarVentasMesConRol1() throws Exception {
        // ARRANGE
        Reporte ventasMes = new Reporte();
        ventasMes.setTipoReporte("VENTAS_MENSUALES");
        ventasMes.setResumen("Mes: 6/2026. Total de pedidos completados: 2");
        ventasMes.setMontoTotal(80000.0);
        ventasMes.setFechaGeneracion(new Date());
        
        when(reporteService.generarReporteVentasMes(6, 2026)).thenReturn(ventasMes);

        // ACT & ASSERT
        mockMvc.perform(post("/v1/orderflow/gestionreportes/generar/ventas-mes/6/2026")
                .header("X-Rol-Empleado", "1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.tipoReporte").value("VENTAS_MENSUALES"))
            .andExpect(jsonPath("$.montoTotal").value(80000.0));
    }

    @Test
    @DisplayName("POST /generar/ventas-mes/{mes}/{anio} - Debe retornar 403 sin Rol 1")
    void testGenerarVentasMesSinRol1() throws Exception {
        mockMvc.perform(post("/v1/orderflow/gestionreportes/generar/ventas-mes/6/2026")
                .header("X-Rol-Empleado", "3")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /generar/ventas-mes/{mes}/{anio} - Debe retornar 500 si el servicio falla")
    void testGenerarVentasMesConError() throws Exception {
        when(reporteService.generarReporteVentasMes(anyInt(), anyInt()))
            .thenThrow(new RuntimeException("Error en BD"));

        mockMvc.perform(post("/v1/orderflow/gestionreportes/generar/ventas-mes/6/2026")
                .header("X-Rol-Empleado", "1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("POST /generar/ventas-mes - Debe aceptar diferentes meses y años")
    void testGenerarVentasMesDiferentesMesesAnios() throws Exception {
        // ARRANGE
        Reporte ventasOctubre = new Reporte();
        ventasOctubre.setTipoReporte("VENTAS_MENSUALES");
        ventasOctubre.setMontoTotal(150000.0);
        ventasOctubre.setFechaGeneracion(new Date());
        
        when(reporteService.generarReporteVentasMes(10, 2025)).thenReturn(ventasOctubre);

        // ACT & ASSERT
        mockMvc.perform(post("/v1/orderflow/gestionreportes/generar/ventas-mes/10/2025")
                .header("X-Rol-Empleado", "1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.montoTotal").value(150000.0));
    }

    

    @Test
    @DisplayName("POST /generar/gastos-mes/{mes}/{anio} - Debe retornar 201 con Rol 1")
    void testGenerarGastosMesConRol1() throws Exception {
        // ARRANGE
        Reporte gastosMes = new Reporte();
        gastosMes.setTipoReporte("GASTOS_PROVEEDORES");
        gastosMes.setResumen("Mes: 6/2026. Total de facturas pagadas: 2");
        gastosMes.setMontoTotal(150000.0);
        gastosMes.setFechaGeneracion(new Date());
        
        when(reporteService.generarReporteGastosMes(6, 2026)).thenReturn(gastosMes);

        // ACT & ASSERT
        mockMvc.perform(post("/v1/orderflow/gestionreportes/generar/gastos-mes/6/2026")
                .header("X-Rol-Empleado", "1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.tipoReporte").value("GASTOS_PROVEEDORES"));
    }

    @Test
    @DisplayName("POST /generar/gastos-mes/{mes}/{anio} - Debe retornar 403 sin Rol 1")
    void testGenerarGastosMesSinRol1() throws Exception {
        mockMvc.perform(post("/v1/orderflow/gestionreportes/generar/gastos-mes/5/2026")
                .header("X-Rol-Empleado", "2")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /generar/gastos-mes/{mes}/{anio} - Debe retornar 500 si el servicio falla")
    void testGenerarGastosMesConError() throws Exception {
        when(reporteService.generarReporteGastosMes(anyInt(), anyInt()))
            .thenThrow(new RuntimeException("Error al obtener facturas"));

        mockMvc.perform(post("/v1/orderflow/gestionreportes/generar/gastos-mes/6/2026")
                .header("X-Rol-Empleado", "1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isInternalServerError());
    }



    @Test
    @DisplayName("GET /historial - Debe retornar 200 con lista de reportes y Rol 1")
    void testObtenerHistorialConRol1() throws Exception {
        // ARRANGE
        Reporte reporte1 = new Reporte();
        reporte1.setTipoReporte("RANKING_PLATOS");
        reporte1.setMontoTotal(10.0);
        
        Reporte reporte2 = new Reporte();
        reporte2.setTipoReporte("VENTAS_MENSUALES");
        reporte2.setMontoTotal(80000.0);
        
        Reporte reporte3 = new Reporte();
        reporte3.setTipoReporte("GASTOS_PROVEEDORES");
        reporte3.setMontoTotal(50000.0);
        
        when(reporteService.listarHistorial())
            .thenReturn(Arrays.asList(reporte1, reporte2, reporte3));

        // ACT & ASSERT
        mockMvc.perform(get("/v1/orderflow/gestionreportes/historial")
                .header("X-Rol-Empleado", "1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    @DisplayName("GET /historial - Debe retornar 403 sin Rol 1")
    void testObtenerHistorialSinRol1() throws Exception {
        mockMvc.perform(get("/v1/orderflow/gestionreportes/historial")
                .header("X-Rol-Empleado", "2")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /historial - Debe retornar lista vacía cuando no hay reportes")
    void testObtenerHistorialVacio() throws Exception {
        when(reporteService.listarHistorial()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/v1/orderflow/gestionreportes/historial")
                .header("X-Rol-Empleado", "1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(0));
    }
}