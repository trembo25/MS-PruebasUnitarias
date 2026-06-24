package cl.duoc.gestionreservas.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class RolEmpleadoDTO {

    @Schema(description = "ID del empleado", examples = {"1"})
    private Integer id;

    @Schema(description = "Nombre del rol del empleado", examples = {"garzon"})
    private String nombreRol;
}
