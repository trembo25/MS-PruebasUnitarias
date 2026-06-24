package cl.duoc.gestionreservas.model;

import java.util.Date;

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
@Table(name = "reserva")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Esquema que representa una reserva dentro del sistema")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único de la reserva", examples = {"1"})
    private Integer id;

    @Column(nullable = false)
    @Schema(description = "Nombre de pila del cliente", examples = {"Juan Alberto"})
    private String nombre;

    @Column(nullable = false)
    @Schema(description = "Apellido paterno del cliente", examples = {"Pérez"})
    private String apellido;

    @Column(nullable = false, unique = true)
    @Schema(description = "RUT del cliente", examples = {"12.345.675-2"})
    private String rut;

    @Column(nullable = false, name = "cantidad_personas")
    @Schema(description = "Cantidad de personas que vendrán a la reserva", examples = {"4"})
    private Integer cantidadPersonas;

    @Column(nullable = false)
    @Schema(description = "Fecha de la reserva", examples = {"20-06-2026"})
    private Date fechaReserva;

    @Column(nullable = false, name = "mesa_id")
    @Schema(description = "Mesa reservada", examples = {"1"})
    private Integer mesaId;

    @Column(nullable = false, name = "empleado_id")
    @Schema(description = "ID del empleado gestor de la reserva", examples = {"2"})
    private Integer empleadoId;
}
