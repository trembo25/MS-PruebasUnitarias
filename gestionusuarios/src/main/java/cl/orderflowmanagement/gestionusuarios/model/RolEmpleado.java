package cl.orderflowmanagement.gestionusuarios.model;

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
@Table(name = "rol_empleado")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Representa el rol de un empleado dentro del sistema")
public class RolEmpleado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rol_id")
    @Schema(description = "ID del rol del empleado", examples = {"1"})
    private Integer id;

    @Column(name = "nombre_rol", nullable = false)
    @Schema(description = "Nombre del rol/cargo del empleado", examples = {"garzon"})
    private String nombreRol;
}
