package cl.orderflowmanagement.gestionpedidos.controller;

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

import cl.orderflowmanagement.gestionpedidos.dto.CantidadRequestDTO;
import cl.orderflowmanagement.gestionpedidos.dto.DetalleRequestDTO;
import cl.orderflowmanagement.gestionpedidos.dto.MetodoPagoRequestDTO;
import cl.orderflowmanagement.gestionpedidos.dto.PedidoReporteDTO;
import cl.orderflowmanagement.gestionpedidos.dto.PedidoRequestDTO;
import cl.orderflowmanagement.gestionpedidos.model.Pedido;
import cl.orderflowmanagement.gestionpedidos.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("v1/orderflow/gestionpedidos")
@Tag(name = "Pedidos", description = "Operaciones sobre las comandas del restaurante")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @GetMapping
    @Operation(
        summary = "Lista todos los pedidos", 
        description = "Devuelve el registro completo de todas las comandas creadas en el sistema"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de pedidos obtenida correctamente"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado. Solo administradores y garzones pueden acceder", content = @Content),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    public ResponseEntity<List<Pedido>> listar(@RequestHeader("X-Rol-Empleado") String rol) {
        if (!rol.equals("1") && !rol.equals("2")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            return ResponseEntity.ok(pedidoService.getPedidos());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    @Operation(
        summary = "Abre un nuevo pedido", 
        description = "Crea una nueva comanda asignando una mesa y el ID del garzón que la atiende"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Pedido creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Error de validación (ej. mesa no disponible o permisos insuficientes)", content = @Content),
        @ApiResponse(responseCode = "403", description = "Acceso denegado. Solo administradores y garzones pueden acceder", content = @Content),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    public ResponseEntity<Pedido> crearPedido(@RequestHeader("X-Rol-Empleado") String rol, @RequestBody PedidoRequestDTO pedidoDTO) {
        if (!rol.equals("1") && !rol.equals("2")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            Pedido pedido = pedidoService.createPedido(pedidoDTO.getMesaId(), pedidoDTO.getEmpleadoId());
            if (pedido != null) {
                return ResponseEntity.status(HttpStatus.CREATED).body(pedido);
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/{pedidoId}/detalles")
    @Operation(
        summary = "Añade un producto a la comanda", 
        description = "Agrega un nuevo detalle (plato o bebida) a un pedido que ya está abierto"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Producto añadido exitosamente al pedido"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado. Rol no autorizado", content = @Content),
        @ApiResponse(responseCode = "404", description = "Pedido o producto no encontrado, o producto no disponible", content = @Content),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    public ResponseEntity<Pedido> aniadirDetalle(@RequestHeader("X-Rol-Empleado") String rol, @PathVariable @Parameter(description = "ID del pedido abierto", required = true, examples = {@ExampleObject(value = "1")}) Integer pedidoId, @RequestBody DetalleRequestDTO detalle) {
        if (!rol.equals("1") && !rol.equals("2")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            Pedido pedido = pedidoService.addProducto(pedidoId, detalle.getProductoId(), detalle.getCantidad());
            if (pedido != null) {
                return ResponseEntity.ok(pedido);
            } else {
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{pedidoId}/detalles/{detalleId}")
    @Operation(
        summary = "Elimina un producto de la comanda", 
        description = "Remueve un detalle específico dentro de un pedido abierto"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Producto eliminado exitosamente del pedido"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado. Rol no autorizado", content = @Content),
        @ApiResponse(responseCode = "404", description = "Pedido o detalle no encontrado, o pedido ya pagado", content = @Content),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    public ResponseEntity<Pedido> eliminarDetalle(@RequestHeader("X-Rol-Empleado") String rol,@PathVariable @Parameter(description = "ID del pedido", required = true, examples = {@ExampleObject(value = "1")}) Integer pedidoId, @PathVariable @Parameter(description = "ID del detalle a eliminar", required = true, examples = {@ExampleObject(value = "10")}) Integer detalleId) {
        if (!rol.equals("1") && !rol.equals("2")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            Pedido pedido = pedidoService.deleteProducto(pedidoId, detalleId);
            if (pedido != null) {
                return ResponseEntity.ok(pedido);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{pedidoId}/detalles/{detalleId}")
    @Operation(
        summary = "Actualiza la cantidad de un producto", 
        description = "Modifica la cantidad solicitada de un producto que ya fue ingresado a la comanda"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cantidad actualizada exitosamente"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado. Rol no autorizado", content = @Content),
        @ApiResponse(responseCode = "404", description = "Pedido o detalle no encontrado, o pedido ya pagado", content = @Content),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    public ResponseEntity<Pedido> actualizarDetalle(@RequestHeader("X-Rol-Empleado") String rol,@PathVariable @Parameter(description = "ID del pedido", required = true, examples = {@ExampleObject(value = "1")}) Integer pedidoId, @PathVariable @Parameter(description = "ID del detalle a modificar", required = true, examples = {@ExampleObject(value = "10")}) Integer detalleId, @RequestBody CantidadRequestDTO cantidad) {
        if (!rol.equals("1") && !rol.equals("2")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            Pedido pedido = pedidoService.updateCantidad(pedidoId, detalleId, cantidad.getCantidad());
            if (pedido != null) {
                return ResponseEntity.ok(pedido);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{pedidoId}/pagar")
    @Operation(
        summary = "Procesa el pago del pedido", 
        description = "Cambia el estado del pedido a 'pagado', se comunica con facturación y libera la mesa asociada"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pago procesado y pedido actualizado exitosamente"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado. Rol no autorizado", content = @Content),
        @ApiResponse(responseCode = "404", description = "Pedido no encontrado, ya pagado o pago rechazado por facturación", content = @Content),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    public ResponseEntity<Pedido> pagar(@RequestHeader("X-Rol-Empleado") String rol, @PathVariable @Parameter(description = "ID del pedido a pagar", required = true, examples = {@ExampleObject(value = "1")}) Integer pedidoId, @RequestBody MetodoPagoRequestDTO metodo) {
        if (!rol.equals("1") && !rol.equals("2")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            Pedido pedido = pedidoService.payPedido(pedidoId,metodo.getMetodoPago());
            if (pedido != null) {
                return ResponseEntity.ok(pedido);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // ESTO ES PARA EL MICROSERVICIO DE REPORTES.
    @GetMapping("/completados")
    @Operation(
        summary = "Lista los pedidos completados (Para reportes)", 
        description = "Devuelve un listado filtrado únicamente con los pedidos que ya fueron pagados. Endpoint de uso exclusivo para el MS de Reportes."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de pedidos completados obtenida exitosamente"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado. Solo el Administrador (1) puede acceder a reportes", content = @Content),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    public ResponseEntity<List<PedidoReporteDTO>> obtenerPedidosCompletados(@RequestHeader("X-Rol-Empleado") String rol) {
        if (!rol.equals("1")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            List<PedidoReporteDTO> pedidosPagados = pedidoService.getPedidosPagadosParaReporte();
            return ResponseEntity.ok(pedidosPagados);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // ESTO ES PARA EL MICROSERVICIO DE REPORTES
    @GetMapping("/detalles-totales")
    @Operation(
        summary = "Lista todos los detalles vendidos (Para reportes)", 
        description = "Obtiene un desglose de todos los productos vendidos en pedidos pagados para generar estadísticas de ventas."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Desglose de detalles de ventas obtenido exitosamente"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado. Solo el Administrador (1) puede acceder a reportes", content = @Content),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    public ResponseEntity<List<DetalleRequestDTO>> obtenerDetallesVentas(@RequestHeader("X-Rol-Empleado") String rol) {
        if (!rol.equals("1")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            List<DetalleRequestDTO> detalles = pedidoService.obtenerTodosLosDetallesParaReporte();
            return ResponseEntity.ok(detalles);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

}
