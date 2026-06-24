package cl.orderflowmanagement.gestionusuarios.controller;

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

import cl.orderflowmanagement.gestionusuarios.model.Empleado;
import cl.orderflowmanagement.gestionusuarios.service.EmpleadoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("v1/orderflow/gestionempleados")
@Tag(name = "Empleados", description = "Operaciones sobre los empleados")
public class EmpleadoController {

    @Autowired
    private EmpleadoService empleadoService;

    @GetMapping
    @Operation(summary = "Lista todos los empleados", 
                description = "Devuelve todos los empleados registrados en el sistema"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de empleados obtenida correctamente"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado. Rol no autorizado", content = @Content),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    public ResponseEntity<List<Empleado>> listar(@RequestHeader("X-Rol-Empleado") String rol) {
        if (!rol.equals("1")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            return ResponseEntity.ok(empleadoService.getAll());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    @Operation(summary = "Agrega un empleado nuevo", 
                description = "Se debe ingresar un esquema de empleado válido"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Empleado creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos del empleado inválidos o incompletos", content = @Content),
        @ApiResponse(responseCode = "403", description = "Acceso denegado. Rol no autorizado", content = @Content),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    public ResponseEntity<Empleado> guardar(@RequestHeader("X-Rol-Empleado") String rol,@RequestBody Empleado empleadoNuevo) {
        if (!rol.equals("1")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        try {
            Empleado empleado = empleadoService.addEmpleado(empleadoNuevo);
            if (empleado != null) {
                return ResponseEntity.status(HttpStatus.CREATED).body(empleado);
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Elimina un empleado por su ID", 
                description = "Elimina físicamente al empleado que coincida con el ID ingresado"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Empleado eliminado con éxito (Sin contenido)"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado. Rol no autorizado", content = @Content),
        @ApiResponse(responseCode = "404", description = "Empleado no encontrado", content = @Content),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    public ResponseEntity<Void> eliminar(@RequestHeader("X-Rol-Empleado") String rol, @PathVariable Integer id) {
        if (!rol.equals("1")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            Empleado empleado = empleadoService.findEmpleadoById(id);
            if (empleado != null) {
                empleadoService.deleteEmpleado(id);
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping
    @Operation(summary = "Actualiza un empleado", 
                description = "Se debe ingresar un modelo de empleado con los datos actualizados y el ID existente"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Empleado actualizado exitosamente"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado. Rol no autorizado", content = @Content),
        @ApiResponse(responseCode = "404", description = "Empleado no encontrado para actualizar", content = @Content),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    public ResponseEntity<Empleado> actualizar(@RequestHeader("X-Rol-Empleado") String rol, @RequestBody Empleado empleadoNuevo) {
        if (!rol.equals("1")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            Empleado empleado = empleadoService.updateEmpleado(empleadoNuevo);
            if (empleado != null) {
                return ResponseEntity.ok(empleado);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // ENDPOINT PARA MICROSERVICIO DE GESTION DE PEDIDOS. (ROJO)
    @GetMapping("/{id}")
    @Operation(summary = "Buscar un empleado por ID", 
                description = "Retorna un empleado según el ID proporcionado"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Empleado encontrado y retornado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Empleado no encontrado", content = @Content),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    public ResponseEntity<?> obtenerPorId(@PathVariable @Parameter(
        description = "Número entero",
        required = true,
        examples = {@ExampleObject(value = "1")}) 
        Integer id) {
        try {
            Empleado empleado = empleadoService.findEmpleadoById(id);
            if (empleado != null) {
                return ResponseEntity.ok(empleado);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

}
