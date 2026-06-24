package cl.ordery.menu.menu_api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "plato")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Entidad de un plato en la base de datos del menú")
public class Plato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plato_id")
    @Schema(description = "ID autogenerado del plato")
    private Integer id;

    @Column(nullable = false, length = 100)
    @Schema(description = "Nombre del plato")
    private String nombre;

    @Column(nullable = false)
    @Schema(description = "Precio total del plato")
    private Double precio;

    @Column(nullable = false)
    @Schema(description = "Estado lógico para mostrar u ocultar del menú al cliente")
    private Boolean disponible;

    @ManyToOne
    @JoinColumn(name = "categoria_id", nullable = false)
    @JsonIgnore 
    @Schema(description = "Referencia a la categoría que agrupa este plato")
    private Categoria categoria;
}