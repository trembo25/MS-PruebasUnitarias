package cl.ordery.menu.menu_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*; 
import lombok.Data;

@Data
@Schema(description = "Objeto de transferencia de datos (dto) que representa un plato específico del menú")
public class PlatoDTO {

    @Schema(description = "Identificador único (id) del plato", example = "1")
    private Integer id;

    @NotBlank(message = "El nombre no puede estar vacio")
    @Schema(description = "Nombre del plato", example = "Lomo a lo Pobre")
    private String nombre;

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor a 0")
    @Schema(description = "Precio bruto (Total a pagar por el cliente)", example = "12500.0")
    private Double precio;

    @NotNull(message = "Debes indicar si esta disponible")
    @Schema(description = "Indicador de disponibilidad en el menú actual", example = "true")
    private Boolean disponible;

    @NotNull(message = "El ID de categoria es obligatorio")
    @Schema(description = "ID de la categoría a la que pertenece este plato", example = "3")
    private Integer categoriaId; 
}