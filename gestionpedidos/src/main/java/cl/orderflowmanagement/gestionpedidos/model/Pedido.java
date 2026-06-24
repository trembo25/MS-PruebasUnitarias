package cl.orderflowmanagement.gestionpedidos.model;

import java.util.Date;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pedido")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Pedido representa una comanda dentro del sistema")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pedido_id")
    @Schema(description = "ID único del pedido", examples = {"1"})
    private Integer id;

    @Column(nullable = false)
    @Schema(description = "Estado del pedido", examples = {"pendiente"})
    private String estado; // ['pendiente', 'pagado']

    @Column(name = "fecha_hora", nullable = false)
    @Schema(description = "Fecha y hora en la que se genera una comanda", examples = {"14-06-2026 20:30"})
    private Date fechaHora;

    @Column(nullable = false)
    @Schema(description = "Total del valor del pedido", examples = {"24990"})
    private Double total;

    @Column(name = "empleado_id", nullable = false)
    @Schema(description = "ID único del empleado que generó esta comanda", examples = {"1"})
    private Integer empleadoId;

    @Column(name = "mesa_id", nullable = false)
    @Schema(description = "ID de la mesa en la que se generó esta comanda", examples = {"1"})
    private Integer mesaId;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true) // orphanremoval elimina completamente el detalle pedido, si se elimina del pedido,
    @Schema(
        description = "Lista de los productos (detalles) asociados a esta comanda", 
        example = "[{cantidad: 2, subtotal: 12000, productoId: 15}]"
    )
    private List<DetallePedido> detalles;

}
