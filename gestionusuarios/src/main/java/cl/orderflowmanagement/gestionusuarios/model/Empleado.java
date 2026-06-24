package cl.orderflowmanagement.gestionusuarios.model;

import java.util.Date;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "empleado")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Empleado representa un empleado dentro del sistema")
public class Empleado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único del empleado", examples = {"1"})
    private Integer id;

    @Column(nullable = false)
    @Schema(description = "Nombre de pila del empleado", examples = {"Juan"})
    private String nombres;

    @Column(nullable = false)
    @Schema(description = "Apellido paterno del empleado", examples = {"Pérez"})
    private String apellidos;

    @Column(nullable = false, name = "fecha_nacimiento")
    private Date fechaNacimiento;

    @Column(nullable = false, name = "fecha_contratacion")
    private Date fechaContratacion;

    @Column(nullable = false, name = "sueldo_base")
    private Double sueldoBase;

    @Column(nullable = false)
    private String direccion;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rol_id", nullable = false)
    private RolEmpleado rolEmpleado;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;
}
