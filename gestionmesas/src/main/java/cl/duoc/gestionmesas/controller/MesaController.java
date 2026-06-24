package cl.duoc.gestionmesas.controller;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.gestionmesas.dto.EstadoMesaDTO;
import cl.duoc.gestionmesas.model.Mesa;
import cl.duoc.gestionmesas.service.MesaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("v1/orderflow/gestionmesas")
@Tag(name = "Mesas", description = "Operaciones sobre las mesas del restaurante")
public class MesaController {

    @Autowired
    private MesaService mesaService;

    @GetMapping
    @Operation(summary = "Lista todas las mesas", 
                description = "Devuelve todas las mesas registradas en el sistema"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de mesas obtenida correctamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    public ResponseEntity<List<Mesa>> listar() {
        try {
            return ResponseEntity.ok(mesaService.listarMesas());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    @Operation(summary = "Agrega una mesa al sistema", 
                description = "Se debe ingresar un esquema de mesa válido para registrarla en la base de datos"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Mesa agregada exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    public ResponseEntity<Mesa> guardar(@RequestBody Mesa mesa) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(mesaService.guardarMesa(mesa));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Elimina una mesa por su ID", 
                description = "Elimina físicamente la mesa que coincida con el ID ingresado"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Mesa eliminada con éxito (Sin contenido)"),
        @ApiResponse(responseCode = "404", description = "Mesa no encontrada", content = @Content)
    })
    public ResponseEntity<Void> eliminar(@PathVariable @Parameter(description = "ID de la mesa a eliminar", required = true, examples = {@ExampleObject(value = "1")}) Integer id) {
        try {
            mesaService.eliminarMesa(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/estado")
    @Operation(summary = "Actualiza el estado de una mesa", 
                description = "Cambia el estado actual (disponible, ocupada, reservada, deshabilitada) de la mesa que coincida con el ID ingresado"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estado de la mesa actualizado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Estado ingresado inválido", content = @Content),
        @ApiResponse(responseCode = "404", description = "Mesa no encontrada", content = @Content)
    })
    public ResponseEntity<Mesa> actualizarEstado(@PathVariable @Parameter(description = "ID de la mesa a actualizar", required = true, examples = {@ExampleObject(value = "1")}) Integer id, @RequestBody EstadoMesaDTO estado) {
        try {
            Mesa mesa = mesaService.actualizarMesa(id, estado.getEstado());
            return ResponseEntity.ok(mesa);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ENDPOINT PARA MICROSERVICIO DE GESTION DE PEDIDOS (ROJO)
    @GetMapping("/{id}")
    @Operation(summary = "Busca una mesa por su ID", 
                description = "Retorna los datos de una mesa según el ID proporcionado"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Mesa encontrada y retornada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Mesa no encontrada", content = @Content)
    })
    public ResponseEntity<?> obtenerPorId(@PathVariable @Parameter(description = "ID de la mesa a buscar", required = true, examples = {@ExampleObject(value = "5")}) Integer id) {
        try {
            return ResponseEntity.ok(mesaService.buscarMesaPorId(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

}
