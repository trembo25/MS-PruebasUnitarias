package cl.orderflowmanagement.gestionreportes.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Objeto de transferencia de datos (DTO) que representa la información resumida de una factura para el módulo de reportes")
public class FacturasDTO {

    @Schema(description = "Identificador único de la factura en el sistema", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer id;

    @Schema(description = "Código alfanumérico único que identifica el documento tributario", example = "FAC-2026-0089", accessMode = Schema.AccessMode.READ_ONLY)
    private String numeroFactura;

    @Schema(description = "Suma final del monto neto más los impuestos de la factura", example = "178500.00", accessMode = Schema.AccessMode.READ_ONLY)
    private Double montoTotal;

    @Schema(description = "Fecha en la que fue emitido el documento por el proveedor", example = "2026-06-18T14:00:00.000Z", accessMode = Schema.AccessMode.READ_ONLY)
    private Date fechaEmision;

    @Schema(
        description = "Estado operativo actual del pedido asociado a esta factura", 
        allowableValues = {"pendiente", "en camino", "completado", "cancelado"}, 
        example = "completado",
        accessMode = Schema.AccessMode.READ_ONLY
    )
    private String estadoPedido;
}