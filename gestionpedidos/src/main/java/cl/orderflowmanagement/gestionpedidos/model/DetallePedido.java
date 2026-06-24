package cl.orderflowmanagement.gestionpedidos.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

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

@Entity
@Table(name = "detalle_pedido")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Representa un detalle específico dentro de la comanda (ej: 2 hamburguesas)")
public class DetallePedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "detalle_pedido_id")
    @Schema(description = "ID único del detalle", examples = {"1"})
    private Integer id;

    @Column(nullable = false)
    @Schema(description = "Cantidad del producto solicitado", examples = {"2"})
    private Integer cantidad;

    @Column(nullable = false)
    @Schema(description = "Subtotal de este detalle (cantidad * precio unitario)", examples = {"12000"})
    private Double subtotal;

    @Column(name = "producto_id", nullable = false)
    @Schema(description = "ID del producto del menú", examples = {"15"})
    private Integer productoId;

    @ManyToOne
    @JoinColumn(name = "pedido_id", nullable = false)
    @JsonIgnore // EVITA BUCLE :c
    @Schema(hidden = true)
    private Pedido pedido;
    
}
