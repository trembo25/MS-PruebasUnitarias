package cl.orderflowmanagement.gestionpedidos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Objeto para modificar la cantidad de un producto ya ingresado en el pedido")
public class CantidadRequestDTO {

    @Schema(description = "Nueva cantidad del producto", example = "3")
    private Integer cantidad;
}
