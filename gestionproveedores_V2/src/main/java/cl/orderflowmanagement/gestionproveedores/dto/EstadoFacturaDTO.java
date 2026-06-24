package cl.orderflowmanagement.gestionproveedores.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "modelo  de estado de facturas ")
public class EstadoFacturaDTO {
    @Schema(description= "Estado de las facturas  con su tipo de estados",
                            allowableValues = {"PENDIENTE, COMPLETADO, CANCELADO"}
                            ,example= "PENDIENTE")
    private String nuevoEstado; // pendiente, en camino, completado, cancelado
}