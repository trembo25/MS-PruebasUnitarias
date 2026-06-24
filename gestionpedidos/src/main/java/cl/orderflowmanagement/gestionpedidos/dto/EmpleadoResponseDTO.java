package cl.orderflowmanagement.gestionpedidos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Datos del empleado obtenidos desde el microservicio de gestión de empleados")
public class EmpleadoResponseDTO {
    
    @Schema(description = "ID del empleado", example = "1")
    private Integer id;

    @Schema(description = "Objeto que contiene los detalles del rol del empleado")
    private RolEmpleadoDTO rolEmpleado;
}
