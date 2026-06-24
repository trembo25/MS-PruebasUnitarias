package cl.orderflowmanagement.gestionpagosyfacturas.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.orderflowmanagement.gestionpagosyfacturas.dto.PagoRequestDTO;
import cl.orderflowmanagement.gestionpagosyfacturas.dto.PagoResponseDTO;
import cl.orderflowmanagement.gestionpagosyfacturas.service.PagoService;

@RestController
@RequestMapping("/v1/orderflow/gestionpagos")
@Tag(name = "Gestión de Pagos", description = "Controlador encargado de procesar las transacciones financieras y emitir comprobantes de pago (boletas)")
public class PagosController {

   @Autowired
   private PagoService pagoService;

    @PostMapping("/hacer-pago")
    @Operation(
        summary = "Procesar el cobro de un pedido", 
        description = "Permite registrar una transacción de pago asociada a un pedido activo y generar su respectiva boleta electrónica. **Nota:** Este endpoint es invocado de forma interna por el Microservicio de Gestión de Pedidos, asumiendo validaciones previas."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pago procesado con éxito. Retorna los datos de la boleta generada.", 
                     content = @Content(schema = @Schema(implementation = PagoResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o malformados.", content = @Content),
        @ApiResponse(responseCode = "500", description = "Error interno al intentar procesar la transacción bancaria o guardar el registro.", content = @Content)
    })
    public ResponseEntity<PagoResponseDTO> cobrarPedido (
        @Valid @RequestBody @Parameter(description = "Datos requeridos para procesar el pago (ID del pedido, monto total y método)") PagoRequestDTO request
    ){
        try {
            PagoResponseDTO boleta = pagoService.generarBoleta(request);
            return ResponseEntity.ok(boleta);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/boleta/{pedidoId}")
    @Operation(
        summary = "Consultar boleta por ID de pedido", 
        description = "Recupera la información detallada del comprobante de pago asociado a un pedido en específico. Requiere validación de Rol de empleado a través de la cabecera HTTP."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Boleta localizada correctamente.", 
                     content = @Content(schema = @Schema(implementation = PagoResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Acceso denegado. El rol del empleado provisto no cuenta con los permisos necesarios (Requiere Rol 1 o 2).", content = @Content),
        @ApiResponse(responseCode = "404", description = "No se encontró ninguna boleta asociada a ese identificador de pedido (el pedido podría no estar pagado aún).", content = @Content),
        @ApiResponse(responseCode = "500", description = "Error interno en el servidor al consultar la base de datos.", content = @Content)
    })
    public ResponseEntity<PagoResponseDTO> verBoleta(
        @RequestHeader("X-Rol-Empleado") @Parameter(description = "Identificador del rol del empleado (Permitidos: '1' o '2')", example = "1") String rol, 
        @PathVariable @Parameter(description = "Identificador único del pedido del cual se desea consultar la boleta", example = "450") Integer pedidoId
    ) {
            
        if (!rol.equals("1") && !rol.equals("2")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            PagoResponseDTO boleta = pagoService.obtenerBoletaPorIdPedido(pedidoId);
            if (boleta == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(boleta);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}