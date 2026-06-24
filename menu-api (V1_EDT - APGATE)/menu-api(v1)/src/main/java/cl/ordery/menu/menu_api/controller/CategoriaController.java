package cl.ordery.menu.menu_api.controller;

import cl.ordery.menu.menu_api.dto.CategoriaDTO;
import cl.ordery.menu.menu_api.service.CategoriaService;
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
@RequestMapping("v1/orderflow/gestionmenu/categorias")
@Tag(name = "Gestión de Categorías", description = "Endpoints para la administración de las categorías del menú")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @GetMapping
    @Operation(summary = "Listar todas las categorías", description = "Retorna una lista con la totalidad de categorías registradas.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente", content = @Content),
        @ApiResponse(responseCode = "403", description = "Acceso denegado.", content = @Content),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    public ResponseEntity<List<CategoriaDTO>> listarTodas(
            @Parameter(description = "Identificador del rol del empleado (Ej: '1' para Admin, '2' para Garzón, '3' para Chef)", required = true)
            @RequestHeader("X-Rol-Empleado") String rol) {
        if (!rol.equals("1") && !rol.equals("2") && !rol.equals("3")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            return ResponseEntity.ok(categoriaService.getCategorias());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener categoría por ID", description = "Busca y retorna los datos de una categoría específica.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Categoría encontrada", content = @Content),
        @ApiResponse(responseCode = "403", description = "Acceso denegado.", content = @Content),
        @ApiResponse(responseCode = "404", description = "Categoría no encontrada", content = @Content),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    public ResponseEntity<CategoriaDTO> obtenerPorId(
            @Parameter(description = "Identificador del rol del empleado (Ej: '1' para Admin, '2' para Garzón, '3' para Chef)", required = true)
            @RequestHeader("X-Rol-Empleado") String rol, 
            @PathVariable Integer id) {
        if (!rol.equals("1") && !rol.equals("2") && !rol.equals("3")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            CategoriaDTO categoriaDto = categoriaService.getCategoriaById(id);
            if (categoriaDto == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(categoriaDto);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    @Operation(summary = "Crear nueva categoría", description = "Registra una nueva categoría en el sistema. Requiere permisos de Admin.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Categoría creada exitosamente", content = @Content),
        @ApiResponse(responseCode = "400", description = "Petición incorrecta o datos inválidos", content = @Content),
        @ApiResponse(responseCode = "403", description = "Acceso denegado.", content = @Content)
    })
    public ResponseEntity<CategoriaDTO> guardar(
            @Parameter(description = "Identificador del rol del empleado (Ej: '1' para Admin)", required = true)
            @RequestHeader("X-Rol-Empleado") String rol, 
            @Valid @RequestBody CategoriaDTO categoriaDto) {
        if (!rol.equals("1")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            CategoriaDTO nueva = categoriaService.createCategoria(categoriaDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar categoría", description = "Modifica los datos de una categoría existente. Requiere permisos de Admin.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Categoría actualizada", content = @Content),
        @ApiResponse(responseCode = "403", description = "Acceso denegado.", content = @Content),
        @ApiResponse(responseCode = "404", description = "Categoría no encontrada", content = @Content),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    public ResponseEntity<CategoriaDTO> actualizar(
            @Parameter(description = "Identificador del rol del empleado (Ej: '1' para Admin)", required = true)
            @RequestHeader("X-Rol-Empleado") String rol, 
            @PathVariable Integer id, 
            @Valid @RequestBody CategoriaDTO categoriaDto) {
        if (!rol.equals("1")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            CategoriaDTO actualizada = categoriaService.actualizarCategoria(id, categoriaDto);
            if (actualizada == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(actualizada);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar categoría", description = "Elimina físicamente una categoría del sistema. Requiere permisos de Admin.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Categoría eliminada con éxito", content = @Content),
        @ApiResponse(responseCode = "403", description = "Acceso denegado.", content = @Content),
        @ApiResponse(responseCode = "404", description = "Categoría no encontrada", content = @Content),
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
            CategoriaDTO existe = categoriaService.getCategoriaById(id);
            if (existe == null) {
                return ResponseEntity.notFound().build();
            }
            categoriaService.deleteCategoria(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}