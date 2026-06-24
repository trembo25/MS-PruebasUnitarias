package cl.orderflowmanagement.gestionreportes.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Objeto de transferencia de datos (DTO) que representa la información financiera clave de un pedido para su uso en reportes estadísticos y de ventas")
public class PedidoReporteDTO {

    @Schema(description = "Identificador único del pedido registrado", example = "450", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer id;

    @Schema(description = "Fecha y hora exacta en la que se generó o completó el pedido", example = "2026-06-18T14:30:00.000Z", accessMode = Schema.AccessMode.READ_ONLY)
    private Date fechaHora;

    @Schema(description = "Monto total final percibido por el pedido (incluyendo impuestos)", example = "119000.00", accessMode = Schema.AccessMode.READ_ONLY)
    private Double total;
}