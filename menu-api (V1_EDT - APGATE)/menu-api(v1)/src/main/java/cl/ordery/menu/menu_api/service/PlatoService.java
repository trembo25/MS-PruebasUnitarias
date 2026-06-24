package cl.ordery.menu.menu_api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.ordery.menu.menu_api.model.Categoria;
import cl.ordery.menu.menu_api.model.Plato;
import cl.ordery.menu.menu_api.dto.PlatoDTO;
import cl.ordery.menu.menu_api.repository.CategoriaRepository;
import cl.ordery.menu.menu_api.repository.PlatoRepository;

import java.util.List;
import java.util.ArrayList; // Para el manejo manual de listas

@Service
@Transactional
public class PlatoService {

    @Autowired
    private PlatoRepository platoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository; 

    // 1. Listar platos transformados a DTO manualmente
    public List<PlatoDTO> getPlatos() {
        List<Plato> platos = platoRepository.findAll();
        List<PlatoDTO> listaDto = new ArrayList<>();
        
        for (Plato p : platos) {
            listaDto.add(convertirADTO(p));
        }
        
        return listaDto;
    }

    // 2. Crear plato usando los datos del DTO
    public PlatoDTO crearPlato(PlatoDTO dto) {
        // Buscamos si la categoria que viene en el DTO existe
        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId()).orElse(null);

        if (categoria == null) {
            return null; 
        }

        // Creamos la entidad Plato y le pasamos los datos del DTO
        Plato plato = new Plato();
        plato.setNombre(dto.getNombre());
        plato.setPrecio(dto.getPrecio());
        plato.setDisponible(dto.getDisponible());
        plato.setCategoria(categoria);
        
        platoRepository.save(plato);
        
        // Devolvemos el DTO del plato guardado
        return convertirADTO(plato);
    }

    // 3. Actualizar disponibilidad
    public PlatoDTO actualizarPlato(Integer platoId, Boolean disponible) {
        Plato plato = platoRepository.findById(platoId).orElse(null);

        if (plato == null) {
            return null;
        }

        plato.setDisponible(disponible);
        platoRepository.save(plato);
        
        return convertirADTO(plato);
    }

    // 4. Buscar un plato por su ID (devuelve DTO)
    public PlatoDTO getPlatoPorId(Integer id) {
        Plato plato = platoRepository.findById(id).orElse(null);
        if (plato != null) {
            return convertirADTO(plato);
        }
        return null;
    }

    // 5. Eliminar un plato por su ID
    public void eliminarPlato(Integer id) {
        platoRepository.deleteById(id);
    }

    /* MÉTODOS DE APOYO  */

    private PlatoDTO convertirADTO(Plato p) {
        PlatoDTO dto = new PlatoDTO();
        dto.setId(p.getId());
        dto.setNombre(p.getNombre());
        dto.setPrecio(p.getPrecio());
        dto.setDisponible(p.getDisponible());
        
        // Solo guardamos el ID de la categoría para el DTO
        if (p.getCategoria() != null) {
            dto.setCategoriaId(p.getCategoria().getId());
        }
        
        return dto;
    }
}