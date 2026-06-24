package cl.orderflowmanagement.gestionproveedores.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description ="modelo del proveedor ")
public class ProveedorDTO {
    @Schema(description = "identificador unico del proveedor",example = "1")
    private Integer id;
    @Schema(description = "RUT del proveedor",example = "26.242.405-k")
    private String rut;
    @Schema(description = "nombre o razon social del proveedor", example = "distribuidora-HH")
    private String nombre;
    @Schema(description  ="correo electronico de contacto del proveedor",example = "distribuidorahh@gmail.com")
    private String correo;
    @Schema(description = "lsita con las facturas correspondientes al proveedor")
    private List<FacturaDTO> facturas; 
}
