package cl.ordery.cocina.gestioncocina.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Objeto de transferencia de datos (dto) para actualizar el estado de una orden")
public class EstadoRequestDTO {
    
    @NotBlank(message = "El nuevo estado no puede estar vacío")
    @Schema(description = "El nuevo estado que se le asignará a la orden (Ej: 'En preparación', 'Listo para servir')", example = "Listo para servir")
    private String nuevoEstado;
}