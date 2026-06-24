package cl.orderflowmanagement.gestionproveedores.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "modelo que representa las facturas resumidas de su respectivo proveedor")
public class FacturaDTO {
    @Schema(description= "identificador unico de las facturas", example= "1" )
    private Integer id;
    @Schema(description= "identificador de la factura",example= "12345")
    private String numeroFactura;
    @Schema(description= "monto total del pedido facturado",example= "120000")
    private Double montoTotal;
    @Schema (description= "estado del pedido asociado a la factura",
     allowableValues = {"PENDIENTE","COMPLETADO", "CANCELADO"},
     example= "PENDIENTE")
    private String estadoPedido;
    @Schema(description= "fecha en la que se emite a factura del pedido",example ="2025-01-13" )
    private Date fechaEmision;
}
