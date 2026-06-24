package cl.ordery.menu.menu_api.controller;

import cl.ordery.menu.menu_api.dto.PlatoDTO;
import cl.ordery.menu.menu_api.service.PlatoService;
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
@RequestMapping("v1/orderflow/gestionmenu/platos")
@Tag(name = "Gestión de Platos", description = "Endpoints para la administración del catálogo de platos")
public class PlatoController {

    @Autowired
    private PlatoService platoService;

    @GetMapping
    @Operation(summary = "Listar todos los platos", description = "Retorna una lista con la totalidad de platos registrados.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente", content = @Content),
        @ApiResponse(responseCode = "403", description = "Acceso denegado.", content = @Content),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    public ResponseEntity<List<PlatoDTO>> listarTodos(
            @Parameter(description = "Identificador del rol del empleado (Ej: '1' para Admin, '2' para Garzón, '3' para Chef)", required = true)
            @RequestHeader("X-Rol-Empleado") String rol) {
        if (!rol.equals("1") && !rol.equals("2") && !rol.equals("3")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            return ResponseEntity.ok(platoService.getPlatos());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener plato por ID", description = "Busca y retorna los datos de un plato específico.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Plato encontrado", content = @Content),
        @ApiResponse(responseCode = "404", description = "Plato no encontrado", content = @Content),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    public ResponseEntity<PlatoDTO> obtenerPorId(@PathVariable Integer id) {
        try {
            PlatoDTO plato = platoService.getPlatoPorId(id);
            if (plato == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(plato);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    @Operation(summary = "Crear nuevo plato", description = "Registra un nuevo plato en el sistema. Requiere permisos de Admin.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Plato creado exitosamente", content = @Content),
        @ApiResponse(responseCode = "400", description = "Petición incorrecta o datos inválidos", content = @Content),
        @ApiResponse(responseCode = "403", description = "Acceso denegado.", content = @Content)
    })
    public ResponseEntity<PlatoDTO> guardar(
            @Parameter(description = "Identificador del rol del empleado (Ej: '1' para Admin)", required = true)
            @RequestHeader("X-Rol-Empleado") String rol, 
            @Valid @RequestBody PlatoDTO platoDto) {
        if (!rol.equals("1")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            PlatoDTO nuevo = platoService.crearPlato(platoDto);
            if (nuevo == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/disponibilidad")
    @Operation(summary = "Actualizar disponibilidad", description = "Cambia el estado de disponibilidad de un plato. Requiere permisos de Admin.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Disponibilidad actualizada", content = @Content),
        @ApiResponse(responseCode = "403", description = "Acceso denegado.", content = @Content),
        @ApiResponse(responseCode = "404", description = "Plato no encontrado", content = @Content),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    public ResponseEntity<PlatoDTO> cambiarEstado(
            @Parameter(description = "Identificador del rol del empleado (Ej: '1' para Admin)", required = true)
            @RequestHeader("X-Rol-Empleado") String rol, 
            @PathVariable Integer id, 
            @RequestParam Boolean disponible) {
        if (!rol.equals("1")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            PlatoDTO actualizado = platoService.actualizarPlato(id, disponible);
            if (actualizado == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(actualizado);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar plato", description = "Elimina físicamente un plato del sistema. Requiere permisos de Admin.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Plato eliminado con éxito", content = @Content),
        @ApiResponse(responseCode = "403", description = "Acceso denegado.", content = @Content),
        @ApiResponse(responseCode = "404", description = "Plato no encontrado", content = @Content),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "Identificador del rol del empleado (Ej: '1' para Admin)", required = true)
            @RequestHeader("X-Rol-Empleado") String rol, 
            @PathVariable Integer id) {
        if (!rol.equals("1")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            PlatoDTO existe = platoService.getPlatoPorId(id);
            if (existe == null) {
                return ResponseEntity.notFound().build();
            }
            platoService.eliminarPlato(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}