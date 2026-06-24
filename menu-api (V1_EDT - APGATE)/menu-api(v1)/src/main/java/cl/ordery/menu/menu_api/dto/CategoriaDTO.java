package cl.ordery.menu.menu_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.List;

@Data
@Schema(description = "Objeto de transferencia de datos (dto) que representa una categoría del menú")
public class CategoriaDTO {

    @Schema(description = "Identificador único (id) de la categoría", example = "1")
    private Integer id;

    @NotBlank(message = "El nombre de la categoria es obligatorio")
    @Size(max = 50)
    @Schema(description = "Nombre visible de la categoría", example = "Bebidas y Jugos")
    private String nombre;

    @NotBlank(message = "La descripcion no puede estar vacia")
    @Schema(description = "Breve descripción de los productos que contiene", example = "Bebidas carbonatadas, jugos naturales y aguas")
    private String descripcion;

    @Schema(description = "Lista de IDs de los platos que pertenecen a esta categoría", example = "[1, 2, 5]")
    private List<Integer> platosIds; 
}