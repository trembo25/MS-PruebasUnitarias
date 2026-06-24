package cl.orderflowmanagement.gestionpagosyfacturas.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.orderflowmanagement.gestionpagosyfacturas.model.Pago;
@Repository
public interface PagoRepository extends JpaRepository<Pago,Integer>{
    Optional<Pago> findByPedidoId(Integer pedidoId);
}
