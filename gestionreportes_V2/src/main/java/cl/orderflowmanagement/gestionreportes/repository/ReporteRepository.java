package cl.orderflowmanagement.gestionreportes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.orderflowmanagement.gestionreportes.model.Reporte;

@Repository
public interface ReporteRepository extends JpaRepository<Reporte, Integer>{

}
