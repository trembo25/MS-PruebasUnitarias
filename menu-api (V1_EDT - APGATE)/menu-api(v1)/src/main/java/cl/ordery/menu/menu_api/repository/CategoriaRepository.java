package cl.ordery.menu.menu_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import cl.ordery.menu.menu_api.model.Categoria;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {
    // Aquí tenemos Listar, Guardar, Eliminar y Buscar por ID por defecto
}