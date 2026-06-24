package cl.ordery.inventario.inventario_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.ordery.inventario.inventario_api.model.Ingrediente;

public interface IngredienteRepository extends JpaRepository<Ingrediente, Integer>{

}
