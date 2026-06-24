package cl.orderflowmanagement.gestionreportes.controller;

import cl.orderflowmanagement.gestionreportes.model.Reporte;
import cl.orderflowmanagement.gestionreportes.service.ReporteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/v1/orderflow/gestionreportes")
@Tag(name = "Gestión de Reportes", description = "Controlador para la generación analítica y consulta del historial de reportes gerenciales del negocio")
public class ReporteController {

    @Autowired
    private ReporteService reporteService;

    @PostMapping("/generar/platoranking")
    @Operation(
        summary = "Generar reporte de ranking de platos", 
        description = "Analiza los pedidos del sistema para consolidar el ranking de los platos más vendidos y almacena el resultado en el historial. **Restricción:** Solo disponible para Rol 1."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Reporte de ranking creado con éxito.", 
                     content = @Content(schema = @Schema(implementation = Reporte.class))),
        @ApiResponse(responseCode = "403", description = "Acceso denegado. Se requiere Rol Administrativo (1).", content = @Content),
        @ApiResponse(responseCode = "400", description = "Error en la solicitud o inconsistencia en los datos procesados.", content = @Content)
    })
    public ResponseEntity<?> generarPlatoRanking(
        @RequestHeader("X-Rol-Empleado") @Parameter(description = "Identificador del rol del empleado (Requerido: '1')", example = "1") String rol
    ) {
        if (!rol.equals("1")) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        try {
            Reporte nuevo = reporteService.generarReportePlatoGanador();
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/generar/ventas-mes/{mes}/{anio}")
    @Operation(
        summary = "Generar reporte de ventas mensual", 
        description = "Consolida las ventas totales, IVA percibido y resúmenes de facturación correspondientes a un mes y año específicos. **Restricción:** Solo disponible para Rol 1."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Reporte de ventas consolidado y guardado correctamente.", 
                     content = @Content(schema = @Schema(implementation = Reporte.class))),
        @ApiResponse(responseCode = "403", description = "Acceso denegado. Se requiere Rol Administrativo (1).", content = @Content),
        @ApiResponse(responseCode = "500", description = "Error interno al procesar o consolidar los datos de ventas.", content = @Content)
    })
    public ResponseEntity<?> generarVentasMes(
        @RequestHeader("X-Rol-Empleado") @Parameter(description = "Identificador del rol del empleado (Requerido: '1')", example = "1") String rol,
        @PathVariable @Parameter(description = "Número del mes a evaluar (1 al 12)", example = "6") int mes, 
        @PathVariable @Parameter(description = "Año de 4 dígitos a evaluar", example = "2026") int anio
    ) {
        if (!rol.equals("1")) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        try {
            Reporte nuevo = reporteService.generarReporteVentasMes(mes, anio);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/generar/gastos-mes/{mes}/{anio}")
    @Operation(
        summary = "Generar reporte de gastos mensual con proveedores", 
        description = "Consolida los montos de facturas emitidas por proveedores externos durante el mes y año provistos. **Restricción:** Solo disponible para Rol 1."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Reporte de gastos consolidado con éxito.", 
                     content = @Content(schema = @Schema(implementation = Reporte.class))),
        @ApiResponse(responseCode = "403", description = "Acceso denegado. Se requiere Rol Administrativo (1).", content = @Content),
        @ApiResponse(responseCode = "500", description = "Error interno en el servidor al compilar egresos financieros.", content = @Content)
    })
    public ResponseEntity<?> generarGastosMes(
        @RequestHeader("X-Rol-Empleado") @Parameter(description = "Identificador del rol del empleado (Requerido: '1')", example = "1") String rol,
        @PathVariable @Parameter(description = "Número del mes a evaluar (1 al 12)", example = "5") int mes, 
        @PathVariable @Parameter(description = "Año de 4 dígitos a evaluar", example = "2026") int anio
    ) {
        if (!rol.equals("1")) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        try {
            Reporte nuevo = reporteService.generarReporteGastosMes(mes, anio);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/historial")
    @Operation(
        summary = "Consultar el historial completo de reportes", 
        description = "Recupera una lista con todos los reportes (ventas, gastos, rankings) que han sido generados históricamente en el sistema. **Restricción:** Solo disponible para Rol 1."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Listado histórico recuperado exitosamente.", 
                     content = @Content(array = @ArraySchema(schema = @Schema(implementation = Reporte.class)))),
        @ApiResponse(responseCode = "403", description = "Acceso denegado. Se requiere Rol Administrativo (1).", content = @Content)
    })
    public ResponseEntity<List<Reporte>> verHistorialReportes(
        @RequestHeader("X-Rol-Empleado") @Parameter(description = "Identificador del rol del empleado (Requerido: '1')", example = "1") String rol
    ) {
        if (!rol.equals("1")) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        return ResponseEntity.ok(reporteService.listarHistorial());
    }
}