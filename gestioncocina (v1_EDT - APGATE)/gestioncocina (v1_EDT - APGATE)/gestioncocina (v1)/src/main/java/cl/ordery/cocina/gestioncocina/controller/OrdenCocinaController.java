package cl.ordery.cocina.gestioncocina.controller;

import cl.ordery.cocina.gestioncocina.dto.EstadoRequestDTO;
import cl.ordery.cocina.gestioncocina.dto.OrdenCocinaDTO;
import cl.ordery.cocina.gestioncocina.model.OrdenCocina;
import cl.ordery.cocina.gestioncocina.service.OrdenCocinaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("v1/orderflow/gestioncocina")
@Tag(name = "Gestión de Cocina", description = "Endpoints para la administración de las comandas y tickets de preparación")
public class OrdenCocinaController {

    @Autowired
    private OrdenCocinaService cocinaSer;

    @GetMapping
    @Operation(summary = "Listar todas las órdenes", description = "Retorna una lista con todos los ordenes de cocina.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de órdenes obtenida exitosamente", content = @Content),
        @ApiResponse(responseCode = "403", description = "Acceso denegado. Se requiere el uso de un rol (Chef o Admin).", content = @Content),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    public ResponseEntity<List<OrdenCocina>> listar(
            @Parameter(description = "Identificador del rol del empleado ('1' para Admin o '3' para Chef)", required = true) 
            @RequestHeader("X-Rol-Empleado") String rol) {
        
        if (!rol.equals("1") && !rol.equals("3")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            return ResponseEntity.ok(cocinaSer.listarOrdenes()); 
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build(); 
        }
    }

    @PostMapping
    @Operation(summary = "Crear nueva orden", description = "Genera una nueva orden de preparación en base al pedido de una mesa.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Orden creada exitosamente", content = @Content),
        @ApiResponse(responseCode = "403", description = "Acceso denegado. Se requiere el uso de un rol (Garzón o Admin).", content = @Content),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    public ResponseEntity<OrdenCocina> crear(
            @Parameter(description = "Identificador del rol del empleado ('1' para Admin o '2' para Garzón)", required = true)
            @RequestHeader("X-Rol-Empleado") String rol, 
            @Valid @RequestBody OrdenCocinaDTO ordenDto) {
        
        if (!rol.equals("2") && !rol.equals("1")) { 
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            OrdenCocina nuevaOrden = new OrdenCocina();
            nuevaOrden.setPedidoId(ordenDto.getPedidoId());
            nuevaOrden.setProductoId(ordenDto.getProductoId());
            nuevaOrden.setCantidad(ordenDto.getCantidad());
            
            OrdenCocina guardada = cocinaSer.guardarOrden(nuevaOrden);
            return ResponseEntity.status(HttpStatus.CREATED).body(guardada);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}/estado")
    @Operation(summary = "Actualizar el estado de la orden", description = "Cambia el estado de preparación de una orden específica.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estado actualizado exitosamente", content = @Content),
        @ApiResponse(responseCode = "400", description = "Petición incorrecta. Falta definir el nuevo estado.", content = @Content),
        @ApiResponse(responseCode = "403", description = "Acceso denegado. Se requiere el uso de un rol (Chef o Admin).", content = @Content),
        @ApiResponse(responseCode = "404", description = "Orden no encontrada", content = @Content),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    public ResponseEntity<OrdenCocina> actualizarEstado(
            @Parameter(description = "Identificador del rol del empleado", required = true)
            @RequestHeader("X-Rol-Empleado") String rol, 
            @PathVariable Integer id, 
            @RequestBody EstadoRequestDTO estadoDto) {

        if (!rol.equals("1") && !rol.equals("3")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); 
        }
        try {
            if (estadoDto == null || estadoDto.getNuevoEstado() == null) {
                return ResponseEntity.badRequest().build(); 
            }
            OrdenCocina actualizada = cocinaSer.actualizarEstado(id, estadoDto.getNuevoEstado());
            if (actualizada != null) {
                return ResponseEntity.ok(actualizada); 
            } else {
                return ResponseEntity.notFound().build(); 
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build(); 
        }
    }
}