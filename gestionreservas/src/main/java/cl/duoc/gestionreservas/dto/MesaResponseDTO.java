package cl.duoc.gestionreservas.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MesaResponseDTO {

    @Schema(description = "ID de la mesa", examples = {"1"})
    private Integer id;

    @Schema(description = "Estado de la mesa", examples = {"disponible"})
    private String estado;

    @Schema(description = "Capacidad de la mesa", examples = {"4"})
    private Integer capacidad;
}
