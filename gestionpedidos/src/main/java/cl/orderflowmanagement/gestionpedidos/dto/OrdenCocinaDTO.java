package cl.orderflowmanagement.gestionpedidos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Objeto enviado a cocina para la preparación de un producto")
public class OrdenCocinaDTO {

    @Schema(description = "ID único de la orden en cocina ", example = "1")
    private Integer id;

    @Schema(description = "ID del pedido/comanda original", example = "1")
    private Integer pedidoId;

    @Schema(description = "ID del producto a preparar", example = "2")
    private Integer productoId;

    @Schema(description = "Cantidad a preparar", example = "2")
    private Integer cantidad;

    @Schema(description = "Estado actual de la preparación", example = "pendiente")
    private String estado;
}
