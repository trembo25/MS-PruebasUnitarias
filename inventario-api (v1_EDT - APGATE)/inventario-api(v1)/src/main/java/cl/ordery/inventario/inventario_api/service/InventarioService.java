package cl.ordery.inventario.inventario_api.service;

import cl.ordery.inventario.inventario_api.client.MenuClient; 
import cl.ordery.inventario.inventario_api.dto.IngredienteDTO;
import cl.ordery.inventario.inventario_api.dto.RecetaDTO;
import cl.ordery.inventario.inventario_api.model.Ingrediente;
import cl.ordery.inventario.inventario_api.model.Receta;
import cl.ordery.inventario.inventario_api.repository.IngredienteRepository;
import cl.ordery.inventario.inventario_api.repository.RecetaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class InventarioService {

    @Autowired
    private IngredienteRepository ingredienteRepo;

    @Autowired
    private RecetaRepository recetaRepo;

    @Autowired
    private MenuClient menuClient; 


    /* --- METODOS DE INGREDIENTE --- */

    //Metodo que lista los [INGREDIENTES]
    public List<IngredienteDTO> listarTodosLosIngredientes() {
        List<Ingrediente> ingredientes = ingredienteRepo.findAll();
        List<IngredienteDTO> listaDtos = new ArrayList<>();
        
        for (Ingrediente ing : ingredientes) {
            IngredienteDTO dto = convertirIngredienteADTO(ing);
            listaDtos.add(dto);
        }
        return listaDtos;
    }

    //Metodo que busca un [INGREDIENTE] por su id
    public IngredienteDTO buscarIngredientePorId(Integer id) {
        Ingrediente ing = ingredienteRepo.findById(id).orElse(null);
        
        if (ing != null) {
            return convertirIngredienteADTO(ing);
        } else {
            return null;
        }
    }

    //Metodo que crea un [INGREDIENTE] 
    public IngredienteDTO crearIngrediente(IngredienteDTO dto) {
        Ingrediente nuevo = new Ingrediente();
        nuevo.setNombre(dto.getNombre());
        nuevo.setStock(dto.getStock());
        nuevo.setUnidad(dto.getUnidad());
        
        ingredienteRepo.save(nuevo);
        return convertirIngredienteADTO(nuevo);
    }

    //Metodo que actualiza un [INGREDIENTE]
    public IngredienteDTO actualizarIngrediente(Integer id, IngredienteDTO dto) {
        Ingrediente ingExistente = ingredienteRepo.findById(id).orElse(null);
        
        if (ingExistente != null) {
            ingExistente.setNombre(dto.getNombre());
            ingExistente.setStock(dto.getStock());
            ingExistente.setUnidad(dto.getUnidad());
            
            ingredienteRepo.save(ingExistente);
            return convertirIngredienteADTO(ingExistente);
        } else {
            return null;
        }
    }

    //Metodo que elimina un [INGREDIENTE]
    public void eliminarIngrediente(Integer id) {
        ingredienteRepo.deleteById(id);
    }

    /* 
    METODOS DE [RECETA]
    */

    //Metodo que lista todas las [RECETAS]
    public List<RecetaDTO> listarTodasLasRecetas() {
        List<Receta> recetas = recetaRepo.findAll();
        List<RecetaDTO> listaDtos = new ArrayList<>();

        for (Receta r : recetas) {
            RecetaDTO dto = convertirRecetaADTO(r);
            listaDtos.add(dto);
        }
        return listaDtos;
    }

    //Metodo que busca las [RECETAS] de un plato especifico
    public List<RecetaDTO> listarRecetasPorPlato(Integer platoId) {
        List<Receta> recetas = recetaRepo.findByPlatoId(platoId);
        List<RecetaDTO> listaDtos = new ArrayList<>();

        for (Receta r : recetas) {
            RecetaDTO dto = convertirRecetaADTO(r);
            listaDtos.add(dto);
        }
        return listaDtos;
    }

    //Metodo que crea una [RECETA] validando el plato con el otro microservicio
    public RecetaDTO crearReceta(RecetaDTO dto) {
        try {
            // Validamos que el plato existe en el micro de Menú usando el cliente Feign
            Object plato = menuClient.buscarPlatoPorId(dto.getPlatoId());
            
            // Buscamos el ingrediente en nuestra BD
            Ingrediente ing = ingredienteRepo.findById(dto.getIngredienteId()).orElse(null);
            
            // Solo creamos si el plato existe afuera y el ingrediente existe acá
            if (plato != null && ing != null) {
                Receta recetaNueva = new Receta();
                
                recetaNueva.setPlatoId(dto.getPlatoId());
                recetaNueva.setIngrediente(ing);
                recetaNueva.setCantidadNecesaria(dto.getCantidadNecesaria());

                recetaRepo.save(recetaNueva);
                return convertirRecetaADTO(recetaNueva);
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    //Metodo que elimina una [RECETA] mediante su id
    public void eliminarReceta(Integer id) {
        recetaRepo.deleteById(id);
    }

    /* 
    LÓGICA DE NEGOCIO
    */

    //Nota: El parámetro platoId corresponde al productoId que se maneja en el micro de Pedidos
    
    //Metodo que descuenta el stock segun los ingredientes del plato
    public boolean descontarStockPorPlato(Integer platoId) {
        List<Receta> recetas = recetaRepo.findByPlatoId(platoId);
        
        if (recetas.isEmpty()) {
            return false;
        }

        for (Receta r : recetas) {
            if (r.getIngrediente().getStock() < r.getCantidadNecesaria()) {
                return false; 
            }
        }

        for (Receta r : recetas) {
            Ingrediente ing = r.getIngrediente();
            double stockFinal = ing.getStock() - r.getCantidadNecesaria();
            ing.setStock(stockFinal);
            ingredienteRepo.save(ing);
        }
        
        return true;
    }

    /* 
    MÉTODOS DE APOYO (MAPEADORES) -> Permiten filtrar los datos que viajan por Internet
    */

    private IngredienteDTO convertirIngredienteADTO(Ingrediente ing) {
        IngredienteDTO dto = new IngredienteDTO();
        dto.setId(ing.getId());
        dto.setNombre(ing.getNombre());
        dto.setStock(ing.getStock());
        dto.setUnidad(ing.getUnidad());
        return dto;
    }

    private RecetaDTO convertirRecetaADTO(Receta rec) {
        RecetaDTO dto = new RecetaDTO();
        dto.setId(rec.getId());
        dto.setPlatoId(rec.getPlatoId());
        dto.setIngredienteId(rec.getIngrediente().getId());
        dto.setCantidadNecesaria(rec.getCantidadNecesaria());
        return dto;
    }
}