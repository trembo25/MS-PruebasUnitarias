package cl.orderflowmanagement.gestionpedidos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Objeto que representa un producto y la cantidad a añadir en la comanda")
public class DetalleRequestDTO {

    @Schema(description = "ID del producto (plato/bebida) del menú", example = "15")
    private Integer productoId;

    @Schema(description = "Cantidad solicitada de este producto", example = "2")
    private Integer cantidad;
}
