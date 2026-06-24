package cl.ordery.inventario.inventario_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Objeto de transferencia de datos (dto) que vincula un plato con sus ingredientes requeridos")
public class RecetaDTO {
    
    @Schema(description = "Identificador único (id) de la receta", example = "1")
    private Integer id;

    @NotNull(message = "El ID del plato es obligatorio")
    @Schema(description = "ID del plato proveniente del microservicio de Menú", example = "10")
    private Integer platoId;

    @NotNull(message = "El ID del ingrediente es obligatorio")
    @Schema(description = "ID del ingrediente registrado en el inventario", example = "2")
    private Integer ingredienteId;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 0, message = "La cantidad debe ser mayor a 0")
    @Schema(description = "Cantidad del ingrediente que se necesita para preparar el plato", example = "0.25")
    private Double cantidadNecesaria;
}