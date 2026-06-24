package cl.orderflowmanagement.gestionpagosyfacturas.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Objeto de transferencia de datos (DTO) representa la respuesta detallada tras procesar un pago con éxito")
public class PagoResponseDTO {
    
    @Schema(
        description = "Estado final del procesamiento de la transacción financiera", 
        allowableValues = {"APROBADO", "RECHAZADO", "PENDIENTE"}, 
        example = "APROBADO",
        accessMode = Schema.AccessMode.READ_ONLY
    )
    private String estado;

    @Schema(description = "Número de folio único de la boleta electrónica generada por el sistema tributario", example = "85241", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer nroBoleta;

    @Schema(description = "Fecha y hora exacta en la que se confirmó y registró la transacción", example = "2026-06-18T14:30:00.000Z", accessMode = Schema.AccessMode.READ_ONLY)
    private Date fechaHora;

    @Schema(description = "Monto total del Impuesto al Valor Agregado (IVA) aplicado a la operación", example = "19000.00", accessMode = Schema.AccessMode.READ_ONLY)
    private Double iva;

    @Schema(description = "Monto final cobrado, liquidado y percibido en la transacción", example = "119000.00", accessMode = Schema.AccessMode.READ_ONLY)
    private Double totalPagado;
}