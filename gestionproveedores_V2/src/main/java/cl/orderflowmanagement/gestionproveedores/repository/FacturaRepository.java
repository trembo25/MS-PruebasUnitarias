package cl.orderflowmanagement.gestionproveedores.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.orderflowmanagement.gestionproveedores.model.Factura;

@Repository
public interface FacturaRepository extends JpaRepository<Factura,Integer>{

}
