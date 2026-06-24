package cl.orderflowmanagement.gestionproveedores.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.orderflowmanagement.gestionproveedores.model.Proveedor;
@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor,Integer>{

}
