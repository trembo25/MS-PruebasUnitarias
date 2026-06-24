package cl.ordery.menu.menu_api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "categoria")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Entidad que representa una categoría de platos en la base de datos")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "categoria_id") 
    @Schema(description = "ID autogenerado de la categoría")
    private Integer id;

    @Column(nullable = false, length = 50)
    @Schema(description = "Nombre de la categoría")
    private String nombre;

    @Column(nullable = false, length = 200)
    @Schema(description = "Descripción de la categoría")
    private String descripcion;

    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL, orphanRemoval = true)
    @Schema(description = "Relación con la entidad Plato. Contiene los platos de esta categoría.")
    private List<Plato> platos = new ArrayList<>();
}