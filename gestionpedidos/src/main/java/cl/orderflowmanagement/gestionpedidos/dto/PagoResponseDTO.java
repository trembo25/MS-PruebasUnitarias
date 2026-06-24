package cl.orderflowmanagement.gestionpedidos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Respuesta del microservicio de facturación tras procesar un pago")
public class PagoResponseDTO {

    @Schema(description = "Estado de la transacción", example = "aprobado")
    private String estado; // "aprobado", "rechazado"

    @Schema(description = "Número de boleta o comprobante generado", example = "784512")
    private Integer nroBoleta; 

    @Schema(description = "Total de la transacción", example = "24990")
    private Double totalPagado;

    // Esto es la "boleta" que se recibira del microservicio de Gabriel, facturacion y pagos
}
