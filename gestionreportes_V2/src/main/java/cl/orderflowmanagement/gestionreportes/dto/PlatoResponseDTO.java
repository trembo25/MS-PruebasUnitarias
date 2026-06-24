package cl.orderflowmanagement.gestionreportes.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Objeto de transferencia de datos (DTO) que representa la información detallada de un plato del menú en las respuestas del sistema")
public class PlatoResponseDTO {

    @Schema(description = "Identificador único del plato en el catálogo del menú", example = "24", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer id;

    @Schema(description = "Nombre comercial del plato gastronómico", example = "Lomo Saltado", accessMode = Schema.AccessMode.READ_ONLY)
    private String nombre;

    @Schema(description = "Precio de venta al público del plato", example = "14500.00", accessMode = Schema.AccessMode.READ_ONLY)
    private Double precio;

    @Schema(description = "Indicador de disponibilidad actual del plato en la cocina para ser ordenado", example = "true", accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean disponible;
}