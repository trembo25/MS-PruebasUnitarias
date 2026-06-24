package cl.ordery.inventario.inventario_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Objeto de transferencia de datos (dto) que representa un ingrediente en el inventario")
public class IngredienteDTO {

    @Schema(description = "Identificador único (id) del ingrediente", example = "1")
    private Integer id;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Schema(description = "Nombre del ingrediente", example = "Pechuga de Pollo")
    private String nombre;

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    @Schema(description = "Cantidad actual disponible en inventario", example = "20.5")
    private Double stock;

    @NotBlank(message = "La unidad (kg, lt, un) es obligatoria")
    @Schema(description = "Unidad de medida del ingrediente", example = "kg")
    private String unidad;
}