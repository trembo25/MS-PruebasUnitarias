package cl.ordery.inventario.inventario_api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "receta")
@Schema(description = "Entidad que define la relación entre un plato del menú y los ingredientes que requiere")
public class Receta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID autogenerado de la receta")
    private Integer id;

    @NotNull(message = "El ID del plato es obligatorio")
    @Column(nullable = false)
    @Schema(description = "Referencia al ID del plato externo")
    private Integer platoId; 

    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad necesaria debe ser mayor a 0")
    @Column(nullable = false)
    @Schema(description = "Cantidad exacta a descontar del inventario al preparar una porción")
    private Double cantidadNecesaria; 

    @NotNull(message = "El ingrediente es obligatorio")
    @ManyToOne
    @JoinColumn(name = "ingrediente_id", nullable = false)
    @Schema(description = "Relación con la entidad Ingrediente para extraer nombre y stock actual")
    private Ingrediente ingrediente;
}