package cl.orderflowmanagement.gestionpedidos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Objeto que define cómo se cancelará la cuenta")
public class MetodoPagoRequestDTO {

    @Schema(description = "Forma de pago utilizada por el cliente", example = "debito")
    private String metodoPago; // credito,debito,efectivo

}
