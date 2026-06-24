package cl.duoc.gestionmesas.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "mesa")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Esquema que representa una mesa dentro del sistema")
public class Mesa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único de la mesa", examples = {"1"})
    private Integer id;

    @Column(nullable = false, unique = true)
    @Schema(description = "Numero asociado a la mesa", examples = {"1"})
    private Integer numero;

    @Column(nullable = false)
    @Schema(description = "Capacidad medida en cantidad de personas que puede tener de la mesa", examples = {"1"})
    private Integer capacidad;

    @Column(nullable = false)
    @Schema(description = "Estado actual de la mesa", examples = {"disponible"})
    private String estado; // disponible; ocupada; reservada; deshabilitada
}
