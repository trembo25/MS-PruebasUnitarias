package cl.ordery.cocina.gestioncocina.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Entity
@Table(name = "orden_cocina")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad que representa un registro de una orden en la base de datos de la cocina")
public class OrdenCocina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único  (id) de la orden de cocina autogenerado")
    private Integer id; 

    @NotNull(message = "El ID del pedido es obligatorio")
    @Column(name = "pedido_id", nullable = false)
    @Schema(description = "Referencia al ID del pedido original")
    private Integer pedidoId;

    @NotNull(message = "El ID del producto es obligatorio")
    @Column(name = "producto_id", nullable = false)
    @Schema(description = "Referencia al ID del plato del menú")
    private Integer productoId;

    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad debe ser mayor a 0")
    @Column(nullable = false)
    @Schema(description = "Cantidad de porciones que el chef debe preparar para este plato específico")
    private Integer cantidad;

    @NotBlank(message = "El estado no puede estar vacío")
    @Column(nullable = false)
    @Schema(description = "Estado actual: [PENDIENTE], [EN PREPARACION] o [LISTO PARA SERVIR]")
    private String estado;

    @Column(name = "fecha_registro", nullable = false)
    @Schema(description = "Fecha y hora exacta en que ingresó la comanda a la cocina")
    private Date fechaRegistro;
}