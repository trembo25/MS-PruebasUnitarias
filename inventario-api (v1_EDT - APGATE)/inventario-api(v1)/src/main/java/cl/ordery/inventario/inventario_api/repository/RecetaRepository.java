package cl.ordery.inventario.inventario_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.ordery.inventario.inventario_api.model.Receta;

public interface RecetaRepository extends JpaRepository<Receta, Integer>{

    List<Receta> findByPlatoId(Integer id);

}
