package cl.ordery.cocina.gestioncocina.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.ordery.cocina.gestioncocina.model.OrdenCocina;

@Repository
public interface OrdenCocinaRepository  extends JpaRepository<OrdenCocina, Integer>{

}
