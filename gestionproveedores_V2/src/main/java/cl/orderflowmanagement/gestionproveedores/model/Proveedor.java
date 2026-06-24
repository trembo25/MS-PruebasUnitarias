package cl.orderflowmanagement.gestionproveedores.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity 
@Table(name="proveedor")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Proveedor {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String rut;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String razonSocial;

    @Column(nullable = false)
    private String direccion;

    @Column(nullable = false)
    private String correo;

    @Column(nullable = false)
    private String telefono;

    @OneToMany(mappedBy = "proveedor", cascade = CascadeType.ALL)
    private List<Factura> facturas = new ArrayList<>();
}
