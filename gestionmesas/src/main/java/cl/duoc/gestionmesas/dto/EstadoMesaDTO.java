package cl.duoc.gestionmesas.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class EstadoMesaDTO {

    @Schema(description = "Estado de la mesa", examples = {"disponible"})
    private String estado; 
}
