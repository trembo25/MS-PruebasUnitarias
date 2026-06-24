package cl.duoc.gestionmesas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.duoc.gestionmesas.model.Mesa;

@Repository
public interface MesaRepository extends JpaRepository<Mesa, Integer>{

}
