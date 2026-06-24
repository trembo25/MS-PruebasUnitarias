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
@Table(name = "usuario")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Representa el usuario de un empleado dentro del sistema")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usuario_id")
    @Schema(description = "ID del usuario creado para un empleado en concreto", examples = {"1"})
    private Integer id;

    @Column(nullable = false, unique = true)
    @Schema(description = "Correo del empleado", examples = {"pepe@orderflow.cl"})
    private String correo;

    @Column(nullable = false)
    @Schema(description = "Contraseña del empleado", examples = {"abc123"})
    private String contrasenia;
}
