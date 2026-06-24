package cl.orderflowmanagement.gestionpedidos.dto;

import java.util.Date;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Objeto resumido del pedido para análisis y reportes")
public class PedidoReporteDTO {

    @Schema(description = "ID del pedido", example = "1")
    private Integer id;

    @Schema(description = "Fecha y hora de la transacción", example = "14-06-2026 20:30")
    private Date fechaHora;

    @Schema(description = "Total facturado en este pedido", example = "24990")
    private Double total;
}
