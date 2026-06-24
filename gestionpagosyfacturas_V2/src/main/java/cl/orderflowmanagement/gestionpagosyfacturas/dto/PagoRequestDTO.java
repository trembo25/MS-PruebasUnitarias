package cl.orderflowmanagement.gestionpagosyfacturas.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Objeto de transferencia de datos (DTO) para la creación de un nuevo registro de pago")
public class PagoRequestDTO {

    @NotNull(message = "El ID del pedido es obligatorio")
    @Schema(description = "Identificador único del pedido que se va a pagar", example = "450")
    private Integer pedidoId;

    @NotNull(message = "El monto total es obligatorio")
    @Positive(message = "El monto total debe ser mayor a cero")
    @Schema(description = "Monto total de la transacción (Neto + IVA)", example = "119000.00")
    private Double total;

    @NotBlank(message = "El método de pago es obligatorio")
    @Schema(
        description = "Medio o plataforma con el que se realiza la transacción", 
        allowableValues = {"tarjeta_credito", "tarjeta_debito", "transferencia", "efectivo"}, 
        example = "tarjeta_debito"
    )
    private String metodoPago;
}