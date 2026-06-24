package cl.orderflowmanagement.gestionpedidos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Datos de la mesa obtenidos desde el microservicio de mesas")
public class MesaResponseDTO {

    @Schema(description = "ID de la mesa", example = "5")
    private Integer id;

    @Schema(description = "Estado actual de la mesa", example = "disponible")
    private String estado;
}
