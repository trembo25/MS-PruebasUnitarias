package cl.ordery.inventario.inventario_api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ingrediente")
@Schema(description = "Entidad que representa un insumo almacenado en la base de datos")
public class Ingrediente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID autogenerado del ingrediente")
    private Integer id;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Column(nullable = false)
    @Schema(description = "Nombre comercial o identificativo del insumo")
    private String nombre;

    @NotNull(message = "El stock es obligatorio")
    @PositiveOrZero(message = "El stock no puede ser negativo")
    @Column(nullable = false)
    @Schema(description = "Stock actual disponible, con validación en base de datos para no ser menor a 0")
    private Double stock;

    @NotBlank(message = "La unidad (kg, lt, un) es obligatoria")
    @Column(nullable = false)
    @Schema(description = "Unidad de medida estandarizada (kg, lt, un)")
    private String unidad;
}