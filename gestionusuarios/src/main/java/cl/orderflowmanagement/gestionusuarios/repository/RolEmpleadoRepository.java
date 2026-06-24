package cl.orderflowmanagement.gestionusuarios.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.orderflowmanagement.gestionusuarios.model.RolEmpleado;

@Repository
public interface RolEmpleadoRepository extends JpaRepository<RolEmpleado, Integer>{

}
