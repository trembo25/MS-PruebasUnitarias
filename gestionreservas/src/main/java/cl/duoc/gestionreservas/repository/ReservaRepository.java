package cl.duoc.gestionreservas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.duoc.gestionreservas.model.Reserva;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Integer>{

}
