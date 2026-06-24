package cl.duoc.gestionreservas.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.gestionreservas.model.Reserva;
import cl.duoc.gestionreservas.service.ReservaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("v1/orderflow/gestionreservas")
@Tag(name = "Reservas", description = "Operaciones sobre las reservas del restaurante")
public class ReservaController {

    @Autowired
    private ReservaService reservaService;

    @GetMapping
    @Operation(summary = "Lista todas las reservas", 
                description = "Devuelve todas las reservas registradas en el sistema"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de reservas obtenida correctamente"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado. Rol no autorizado", content = @Content),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    public ResponseEntity<List<Reserva>> listar(@RequestHeader("X-Rol-Empleado") String rol) {
        if (!rol.equals("1") && !rol.equals("4")) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        try {
            return ResponseEntity.ok(reservaService.listarReservas());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    @Operation(summary = "Guarda una reserva nueva", 
                description = "Se debe ingresar un esquema de reserva válido. Valida disponibilidad de mesa y existencia del empleado."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Reserva creada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Error de validación (ej. mesa no disponible o capacidad insuficiente)", content = @Content),
        @ApiResponse(responseCode = "403", description = "Acceso denegado. Solo Admin (1) y Recepción (4) pueden guardar reservas", content = @Content),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    public ResponseEntity<?> guardar(@RequestHeader("X-Rol-Empleado") String rol, @RequestBody Reserva reserva) {
        // Solo Admin (1) y Recepción (4) manejan reservas
        if (!rol.equals("1") && !rol.equals("4")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permisos de Administrador o Recepción.");
        }
        try {
            Reserva reservaComprobacion = reservaService.guardarReserva(reserva);
            return ResponseEntity.status(HttpStatus.CREATED).body(reservaComprobacion);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Elimina una reserva por su ID", 
                description = "Elimina la reserva que coincida con el ID ingresado y libera la mesa asociada"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Reserva eliminada con éxito (Sin contenido)"),
        @ApiResponse(responseCode = "400", description = "Error de validación al intentar eliminar la reserva", content = @Content),
        @ApiResponse(responseCode = "403", description = "Acceso denegado. Rol no autorizado", content = @Content),
        @ApiResponse(responseCode = "404", description = "Reserva no encontrada", content = @Content)
    })
    public ResponseEntity<?> eliminar(@RequestHeader("X-Rol-Empleado") String rol, @PathVariable @Parameter(description = "ID de la reserva a eliminar", required = true, examples = {@ExampleObject(value = "1")}) Integer id) {
        if (!rol.equals("1") && !rol.equals("4")) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        try {
            reservaService.eliminarReserva(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping
    @Operation(summary = "Actualiza una reserva", 
                description = "Se debe ingresar un esquema de reserva nuevo, validando que la mesa de destino esté disponible si hay un cambio."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reserva actualizada exitosamente"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado. Rol no autorizado", content = @Content),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor al procesar la actualización", content = @Content)
    })
    public ResponseEntity<Reserva> actualizar(@RequestHeader("X-Rol-Empleado") String rol, @RequestBody Reserva reservaActualizada) {
        if (!rol.equals("1") && !rol.equals("4")) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        try {
            return ResponseEntity.ok(reservaService.actualizarReserva(reservaActualizada));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    
    @GetMapping("id/{id}")
    @Operation(summary = "Busca una reserva por ID", 
                description = "Retorna la reserva que coincida con el ID ingresado."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reserva encontrada y retornada exitosamente"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado. Rol no autorizado", content = @Content),
        @ApiResponse(responseCode = "404", description = "Reserva no encontrada", content = @Content)
    })
    public ResponseEntity<Reserva> buscarPorId(@RequestHeader("X-Rol-Empleado") String rol, @PathVariable @Parameter(description = "ID de la reserva a buscar", required = true, examples = {@ExampleObject(value = "1")}) Integer id) {
        if (!rol.equals("1") && !rol.equals("4")) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        try {
            return ResponseEntity.ok(reservaService.buscarPorId(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
