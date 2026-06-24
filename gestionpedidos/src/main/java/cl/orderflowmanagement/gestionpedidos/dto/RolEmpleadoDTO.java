package cl.orderflowmanagement.gestionpedidos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Schema(description = "Define el rol y nivel de acceso de un empleado")
@AllArgsConstructor
@NoArgsConstructor
public class RolEmpleadoDTO {

    @Schema(description = "ID del rol (1 = Admin, 2 = Garzón)", example = "2")
    private Integer id;

    @Schema(description = "Nombre del rol", example = "garzon")
    private String nombreRol;
}
