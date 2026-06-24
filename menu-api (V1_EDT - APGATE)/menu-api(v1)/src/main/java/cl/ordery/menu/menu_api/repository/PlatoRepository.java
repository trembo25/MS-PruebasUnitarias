package cl.ordery.menu.menu_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import cl.ordery.menu.menu_api.model.Plato;

@Repository
public interface PlatoRepository extends JpaRepository<Plato, Integer> {
}