package cl.duoc.gestionreservas.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class EmpleadoResponseDTO {

    @Schema(description = "ID del empleado", examples = {"1"})
    private Integer id;

    @Schema(description = "Rol del empleado", examples = {"{id:1,nombreRol: 'garzon'}"})
    private RolEmpleadoDTO rolEmpleado;
}
