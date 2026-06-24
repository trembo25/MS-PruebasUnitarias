package cl.ordery.menu.menu_api.service;

import cl.ordery.menu.menu_api.model.Categoria;
import cl.ordery.menu.menu_api.model.Plato;
import cl.ordery.menu.menu_api.dto.CategoriaDTO;
import cl.ordery.menu.menu_api.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * El service solo se encarga de manejar la lógica de negocio de las Categorías del menú.
 * Actúa como intermediario entre el controlador y la base de datos.
 */
@Service
@Transactional
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    /**
     * 1. Listar todas las categorias disponibles.
     * Extrae todas las entidades de la BD y las transforma a DTOs para la respuesta HTTP.
     */
    public List<CategoriaDTO> getCategorias() {
        return categoriaRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * 2. Buscar una categoria especifica por ID.
     * Retorna el DTO de la categoría si existe, o null si no se encuentra.
     */
    public CategoriaDTO getCategoriaById(Integer id) {
        Categoria categoria = categoriaRepository.findById(id).orElse(null);
        return (categoria != null) ? convertirADTO(categoria) : null;
    }

    /**
     * 3. Guardar una nueva categoria.
     * Recibe los datos filtrados desde el DTO, crea la entidad y la guarda de manera persistente en la BD.
     */
    public CategoriaDTO createCategoria(CategoriaDTO dto) {
        Categoria categoria = new Categoria();
        categoria.setNombre(dto.getNombre());
        categoria.setDescripcion(dto.getDescripcion());
        
        categoriaRepository.save(categoria);
        return convertirADTO(categoria);
    }

    /**
     * 4. Actualizar una categoria existente.
     * Busca la entidad por su ID y reemplaza sus valores enviados en el DTO.
     */
    public CategoriaDTO actualizarCategoria(Integer id, CategoriaDTO categoriaDetalles) {
        Categoria categoria = categoriaRepository.findById(id).orElse(null);

        if (categoria == null) {
            return null; 
        }

        // Actualizamos solo los datos que corresponden
        categoria.setNombre(categoriaDetalles.getNombre());
        categoria.setDescripcion(categoriaDetalles.getDescripcion());
        
        categoriaRepository.save(categoria);
        return convertirADTO(categoria);
    }

    /**
     * 5. Borrar categoria.
     * Elimina el registro de la categoria en la base de datos mediante su ID.
     */
    public void deleteCategoria(Integer id) {
        categoriaRepository.deleteById(id);
    }

    /* ---------------------------------------------------
     * MÉTODOS DE APOYO (MAPEADORES)
     * --------------------------------------------------- */

    /**
     * Convierte una entidad Categoria de la BD a un CategoriaDTO para enviar al cliente.
     * Evita un bucle infinito
     */
    private CategoriaDTO convertirADTO(Categoria categoria) {
        CategoriaDTO dto = new CategoriaDTO();
        dto.setId(categoria.getId());
        dto.setNombre(categoria.getNombre());
        dto.setDescripcion(categoria.getDescripcion());
        
        // Extraemos los IDs de los platos de forma manual
        if (categoria.getPlatos() != null) {
            List<Integer> ids = new ArrayList<>();
            for (Plato p : categoria.getPlatos()) {
                // Sacamos solo el ID del plato y lo guardamos en nuestra lista
                ids.add(p.getId());
            }
            dto.setPlatosIds(ids);
        }
        
        return dto;
    }
}