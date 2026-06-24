package cl.orderflowmanagement.gestionproveedores.controller;

import cl.orderflowmanagement.gestionproveedores.dto.EstadoFacturaDTO;
import cl.orderflowmanagement.gestionproveedores.dto.FacturaDTO;
import cl.orderflowmanagement.gestionproveedores.dto.ProveedorDTO;
import cl.orderflowmanagement.gestionproveedores.model.Factura;
import cl.orderflowmanagement.gestionproveedores.model.Proveedor;
import cl.orderflowmanagement.gestionproveedores.service.ProveedorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/v1/orderflow/gestionproveedores")
@Tag(name = "Gestión de Proveedores y Facturas", description = "Endpoints para la administración de proveedores y sus respectivos documentos de facturación")
public class ProveedorController {

    @Autowired
    private ProveedorService proveedorService;

    @GetMapping
    @Operation(summary = "Listar todos los proveedores", description = "Retorna una lista con la totalidad de los proveedores registrados. Requiere rol de Administrador.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de proveedores obtenida exitosamente",content = @Content),
        @ApiResponse(responseCode = "403", description = "Acceso denegado. Rol insuficiente.", content = @Content),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    public ResponseEntity<List<ProveedorDTO>> listar(
            @Parameter(description = "Identificador del rol del empleado (Ej: '1' para Admin)", required = true, example = "1")
            @RequestHeader("X-Rol-Empleado") String rol) {
        if (!rol.equals("1")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            return ResponseEntity.ok(proveedorService.listarTodos());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar proveedor por ID", description = "Obtiene los datos de un proveedor específico según su identificador único.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Proveedor encontrado exitosamente"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content),
        @ApiResponse(responseCode = "404", description = "Proveedor no encontrado", content = @Content),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    public ResponseEntity<ProveedorDTO> buscar(
            @Parameter(description = "Rol del empleado", required = true, example = "1") @RequestHeader("X-Rol-Empleado") String rol,
            @Parameter(description = "ID del proveedor", required = true, examples = @ExampleObject(value = "5")) @PathVariable Integer id) {
        if (!rol.equals("1")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            ProveedorDTO dto = proveedorService.buscarPorId(id);
            return (dto != null) ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    @Operation(summary = "Registrar proveedor", description = "Crea un nuevo registro de proveedor en la base de datos.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Proveedor creado correctamente"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    public ResponseEntity<ProveedorDTO> agregarProveedor(
            @Parameter(description = "Rol del empleado", required = true, example = "1") @RequestHeader("X-Rol-Empleado") String rol,
            @RequestBody Proveedor proveedor) {
        if (!rol.equals("1")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            ProveedorDTO nuevoProveedor = proveedorService.guardarProveedor(proveedor);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProveedor);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/facturas")
    @Operation(summary = "Registrar factura", description = "Crea una nueva factura asociada a un proveedor.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Factura creada correctamente"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    public ResponseEntity<FacturaDTO> agregarFactura(
            @Parameter(description = "Rol del empleado", required = true, example = "1") @RequestHeader("X-Rol-Empleado") String rol,
            @RequestBody Factura factura) {
        if (!rol.equals("1")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            FacturaDTO nuevaFactura = proveedorService.guardarFactura(factura);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaFactura);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar proveedor", description = "Elimina un proveedor de la base de datos según su ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Proveedor eliminado correctamente"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    public ResponseEntity<Void> eliminarProveedor(
            @Parameter(description = "Rol del empleado", required = true, example = "1") @RequestHeader("X-Rol-Empleado") String rol,
            @Parameter(description = "ID del proveedor a eliminar", required = true, example = "3") @PathVariable Integer id) {
        if (!rol.equals("1")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            proveedorService.eliminarProveedor(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/facturas/{facturaId}/estado")
    @Operation(summary = "Actualizar estado de factura", description = "Modifica el estado de una factura (Ej: 'pendiente' a 'pagada').")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estado actualizado correctamente"),
        @ApiResponse(responseCode = "400", description = "Estado inválido", content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content),
        @ApiResponse(responseCode = "404", description = "Factura no encontrada", content = @Content),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    public ResponseEntity<?> actualizarEstadoFactura(
            @Parameter(description = "Rol del empleado", required = true, example = "1") @RequestHeader("X-Rol-Empleado") String rol,
            @Parameter(description = "ID de la factura", required = true, example = "12") @PathVariable Integer facturaId,
            @RequestBody EstadoFacturaDTO estadoDto) {
        if (!rol.equals("1")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            FacturaDTO actualizada = proveedorService.actualizarEstadoFactura(facturaId, estadoDto.getNuevoEstado());
            return (actualizada != null) ? ResponseEntity.ok(actualizada) : ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

   

    @GetMapping("/facturas")
    @Operation(summary = "Listar todas las facturas", description = "Devuelve el listado completo de facturas registradas en el sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Listado de facturas obtenido correctamente"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)})
    public ResponseEntity<List<FacturaDTO>> listarTodasLasFacturas(@RequestHeader("X-Rol-Empleado") String rol) {
        if (!rol.equals("1")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            return ResponseEntity.ok(proveedorService.listarTodasLasFacturas());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}