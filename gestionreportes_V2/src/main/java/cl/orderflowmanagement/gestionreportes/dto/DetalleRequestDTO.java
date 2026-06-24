package cl.orderflowmanagement.gestionreportes.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Objeto de transferencia de datos (DTO) que representa el desglose de un producto específico dentro de una solicitud")
public class DetalleRequestDTO {

    @NotNull(message = "El ID del producto es obligatorio")
    @Schema(description = "Identificador único del producto o artículo en el sistema", example = "105")
    private Integer productoId;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad mínima requerida debe ser al menos 1")
    @Schema(description = "Número de unidades solicitadas o procesadas del producto", example = "3")
    private Integer cantidad;
}