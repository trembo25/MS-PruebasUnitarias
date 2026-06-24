package cl.orderflowmanagement.gestionpedidos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Objeto enviado al sistema de facturación para procesar el pago")
public class PagoRequestDTO {

    @Schema(description = "ID del pedido que se está pagando", example = "1")
    private Integer pedidoId;

    @Schema(description = "Monto total a cobrar", example = "24990")
    private Double total;

    @Schema(description = "Método de pago seleccionado por el cliente", example = "debito")
    private String metodoPago;
    // se puede agregar metodo de pago dsp, o tambien el clienteid
    // depende de que quiera el microservicio de pagos y facturacion
}
