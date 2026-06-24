package cl.orderflowmanagement.gestionpedidos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Datos del plato obtenidos desde el microservicio de menú")
public class PlatoResponseDTO {

    @Schema(description = "ID del plato", example = "1")
    private Integer id;

    @Schema(description = "Nombre del plato o producto", example = "Hamburguesa Doble")
    private String nombre;

    @Schema(description = "Precio unitario del producto", example = "6000")
    private Double precio;

    @Schema(description = "Indica si el producto tiene stock en cocina", example = "true")
    private Boolean disponible;
}
