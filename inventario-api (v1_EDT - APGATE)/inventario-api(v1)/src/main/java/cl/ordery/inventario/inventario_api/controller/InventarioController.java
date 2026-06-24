package cl.ordery.inventario.inventario_api.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cl.ordery.inventario.inventario_api.dto.IngredienteDTO;
import cl.ordery.inventario.inventario_api.dto.RecetaDTO;
import cl.ordery.inventario.inventario_api.service.InventarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("v1/orderflow/gestioninventario")
@Tag(name = "Gestión de Inventario", description = "Endpoints para la administración de insumos, recetas y stock")
public class InventarioController {

    @Autowired
    private InventarioService inventarioService;

    /* --- METODOS DE INGREDIENTE --- */

    @GetMapping("/ingredientes")
    @Operation(summary = "Listar ingredientes", description = "Retorna el inventario completo de ingredientes.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista obtenida", content = @Content),
        @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content),
        @ApiResponse(responseCode = "500", description = "Error interno", content = @Content)
    })
    public ResponseEntity<List<IngredienteDTO>> listar(
            @Parameter(description = "Rol del empleado (Ej: '1' Admin, '2' Garzón, '3' Chef)", required = true)
            @RequestHeader("X-Rol-Empleado") String rol) {
        if (!rol.equals("1") && !rol.equals("2") && !rol.equals("3")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            return ResponseEntity.ok(inventarioService.listarTodosLosIngredientes());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/ingredientes/{id}")
    @Operation(summary = "Buscar ingrediente", description = "Busca un ingrediente específico mediante su id.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Ingrediente encontrado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content),
        @ApiResponse(responseCode = "404", description = "Ingrediente no encontrado", content = @Content),
        @ApiResponse(responseCode = "500", description = "Error interno", content = @Content)
    })
    public ResponseEntity<IngredienteDTO> buscarPorId(
            @Parameter(description = "Rol del empleado (Ej: '1' Admin, '2' Garzón, '3' Chef)", required = true)
            @RequestHeader("X-Rol-Empleado") String rol, @PathVariable Integer id) {
        if (!rol.equals("1") && !rol.equals("2") && !rol.equals("3")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            IngredienteDTO dto = inventarioService.buscarIngredientePorId(id);
            if (dto != null) {
                return ResponseEntity.ok(dto);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/ingredientes")
    @Operation(summary = "Crear ingrediente", description = "Registra un nuevo insumo. Requiere permisos de Admin.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Ingrediente creado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content),
        @ApiResponse(responseCode = "500", description = "Error interno", content = @Content)
    })
    public ResponseEntity<IngredienteDTO> crearIngrediente(
            @Parameter(description = "Requiere rol de admin", required = true)
            @RequestHeader("X-Rol-Empleado") String rol, @Valid @RequestBody IngredienteDTO dto) {
        if (!rol.equals("1")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            IngredienteDTO ingCreado = inventarioService.crearIngrediente(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(ingCreado);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/ingredientes/{id}")
    @Operation(summary = "Actualizar ingrediente", description = "Modifica los datos/stock de un insumo. Requiere permisos de Admin.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Ingrediente actualizado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content),
        @ApiResponse(responseCode = "404", description = "Ingrediente no encontrado", content = @Content),
        @ApiResponse(responseCode = "500", description = "Error interno", content = @Content)
    })
    public ResponseEntity<IngredienteDTO> actualizar(
            @Parameter(description = "Requiere rol de admin", required = true)
            @RequestHeader("X-Rol-Empleado") String rol, @PathVariable Integer id, @Valid @RequestBody IngredienteDTO dto) {
        if (!rol.equals("1")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            IngredienteDTO ingActualizado = inventarioService.actualizarIngrediente(id, dto);
            if (ingActualizado != null) {
                return ResponseEntity.ok(ingActualizado);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/ingredientes/{id}")
    @Operation(summary = "Eliminar ingrediente", description = "Elimina un insumo del inventario. Requiere permisos de Admin.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Ingrediente eliminado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content),
        @ApiResponse(responseCode = "500", description = "Error interno", content = @Content)
    })
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "Requiere rol de admin", required = true)
            @RequestHeader("X-Rol-Empleado") String rol, @PathVariable Integer id) {
        if (!rol.equals("1")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            inventarioService.eliminarIngrediente(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /* --- METODOS DE RECETA --- */

    @GetMapping("/recetas")
    @Operation(summary = "Listar recetas", description = "Retorna el listado completo de recetas.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista obtenida", content = @Content),
        @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content),
        @ApiResponse(responseCode = "500", description = "Error interno", content = @Content)
    })
    public ResponseEntity<List<RecetaDTO>> listarRecetas(
            @Parameter(description = "Rol del empleado", required = true)
            @RequestHeader("X-Rol-Empleado") String rol) {
        if (!rol.equals("1") && !rol.equals("2") && !rol.equals("3")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            return ResponseEntity.ok(inventarioService.listarTodasLasRecetas());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/recetas/plato/{platoId}")
    @Operation(summary = "Listar recetas por plato", description = "Busca todos los ingredientes que componen un plato específico.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Recetas obtenidas", content = @Content),
        @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content),
        @ApiResponse(responseCode = "500", description = "Error interno", content = @Content)
    })
    public ResponseEntity<List<RecetaDTO>> listarPorPlato(
            @Parameter(description = "Rol del empleado", required = true)
            @RequestHeader("X-Rol-Empleado") String rol, @PathVariable Integer platoId) {
        if (!rol.equals("1") && !rol.equals("2") && !rol.equals("3")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            return ResponseEntity.ok(inventarioService.listarRecetasPorPlato(platoId));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/recetas")
    @Operation(summary = "Crear receta", description = "Registra una nueva receta que conecta Plato e Ingrediente. Requiere permisos de Admin.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Receta creada", content = @Content),
        @ApiResponse(responseCode = "400", description = "Petición incorrecta", content = @Content),
        @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content),
        @ApiResponse(responseCode = "500", description = "Error interno", content = @Content)
    })
    public ResponseEntity<RecetaDTO> crearReceta(
            @Parameter(description = "Requiere rol de admin", required = true)
            @RequestHeader("X-Rol-Empleado") String rol, @Valid @RequestBody RecetaDTO dto) {
        if (!rol.equals("1")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            RecetaDTO recetaCreada = inventarioService.crearReceta(dto);
            if (recetaCreada != null) {
                return ResponseEntity.status(HttpStatus.CREATED).body(recetaCreada);
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/recetas/{id}")
    @Operation(summary = "Eliminar receta", description = "Elimina una receta del sistema. Requiere permisos de Admin.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Receta eliminada", content = @Content),
        @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content),
        @ApiResponse(responseCode = "500", description = "Error interno", content = @Content)
    })
    public ResponseEntity<Void> eliminarReceta(
            @Parameter(description = "Requiere rol de admin", required = true)
            @RequestHeader("X-Rol-Empleado") String rol, @PathVariable Integer id) {
        if (!rol.equals("1")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            inventarioService.eliminarReceta(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /* --- LOGICA DE NEGOCIO (DESCUENTO DE STOCK) --- */

    @PostMapping("/descontar/{platoId}")
    @Operation(summary = "Descontar stock interno", description = "Endpoint de comunicación interna. Descuenta del inventario los ingredientes de un plato generado por Pedidos o Cocina.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Stock descontado exitosamente", content = @Content),
        @ApiResponse(responseCode = "400", description = "Error al descontar (Ej. Stock insuficiente)", content = @Content),
        @ApiResponse(responseCode = "500", description = "Error interno", content = @Content)
    })
    public ResponseEntity<Void> descontarStock(@PathVariable Integer platoId) {
        try {
            boolean exito = inventarioService.descontarStockPorPlato(platoId);
            if (exito) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}