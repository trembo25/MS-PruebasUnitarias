package cl.ordery.cocina.gestioncocina.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Objeto de transferencia de datos (dto) que representa una comanda de cocina")
public class OrdenCocinaDTO {
    
    @Schema(description = "Identificador único (id) de la orden (Autogenerado, no enviar al crear)", example = "1")
    private Integer id;
    
    @NotNull(message = "El ID del pedido es obligatorio")
    @Schema(description = "ID del pedido general proveniente del microservicio de Gestión de Pedidos", example = "105")
    private Integer pedidoId;
    
    @NotNull(message = "El ID del producto es obligatorio")
    @Schema(description = "ID del plato específico proveniente del microservicio de Menú", example = "12")
    private Integer productoId; 
    
    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad debe ser mayor a 0")
    @Schema(description = "Cantidad de porciones a preparar de este plato", example = "2")
    private Integer cantidad; 
    
    @NotBlank(message = "El estado no puede estar vacío")
    @Schema(description = "Estado actual de la orden en la cocina", example = "Pendiente")
    private String estado; 
}