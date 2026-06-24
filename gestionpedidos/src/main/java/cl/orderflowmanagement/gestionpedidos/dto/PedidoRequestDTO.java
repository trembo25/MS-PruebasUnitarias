package cl.orderflowmanagement.gestionpedidos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Objeto requerido para abrir una nueva comanda en una mesa")
public class PedidoRequestDTO {

    @Schema(description = "ID de la mesa donde se sentarán los clientes", example = "5")
    private Integer mesaId;

    @Schema(description = "ID del empleado (Garzón) que está tomando el pedido", example = "1")
    private Integer empleadoId;
}
