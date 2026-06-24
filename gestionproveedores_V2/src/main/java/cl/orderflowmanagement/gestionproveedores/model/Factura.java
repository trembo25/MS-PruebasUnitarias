package cl.orderflowmanagement.gestionproveedores.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "facturas")
@Schema(description = "Modelo que representa la información financiera y de facturación de un proveedor")
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único autoincrementable de la factura", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer id;

    @Column(nullable = false)
    @Schema(description = "Código alfanumérico único que identifica el documento tributario", example = "FAC-2026-0089")
    private String numeroFactura;

    @Column(nullable = false)
    @Schema(description = "Fecha en la que se emitió el documento", example = "2026-06-02T00:00:00.000Z")
    private Date fechaEmision;

    @Column(nullable = false)
    @Schema(description = "Fecha límite para realizar el pago de la factura", example = "2026-07-02T00:00:00.000Z")
    private Date fechaVencimiento;

    @Column(nullable = false)
    @Schema(description = "Monto de los productos antes de aplicar impuestos", example = "150000.00")
    private Double montoNeto;

    @Column(nullable = false)
    @Schema(description = "Valor del impuesto al valor agregado (IVA) calculado", example = "28500.00")
    private Double iva;

    @Schema(description = "Suma final del monto neto más el IVA correspondiente", example = "178500.00")
    private Double montoTotal;

    @Column(nullable = false)
    @Schema(
        description = "Estado operativo actual del pedido asociado a la factura", 
        allowableValues = {"pendiente", "en camino", "completado", "cancelado"}, 
        example = "pendiente"
    )
    private String estadoPedido;

    @ManyToOne
    @JoinColumn(name = "proveedor_id")
    @Schema(description = "Información resumida del proveedor asociado que emite el cobro")
    private Proveedor proveedor;
}
