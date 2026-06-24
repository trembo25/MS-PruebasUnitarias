package cl.ordery.cocina.gestioncocina.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.ordery.cocina.gestioncocina.model.OrdenCocina;
import cl.ordery.cocina.gestioncocina.repository.OrdenCocinaRepository;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class OrdenCocinaService {

    @Autowired
    private OrdenCocinaRepository cocinaRepo;

    // Metodo que lista todas las ordenes
    public List<OrdenCocina> listarOrdenes(){
        return cocinaRepo.findAll();
    }

    // Metodo que guarda (o registra) una orden
    public OrdenCocina guardarOrden(OrdenCocina ordenCocina){
        
        //Le pasamos el estado inicial de [PENDIENTE]
        ordenCocina.setEstado("Pendiente");

        ordenCocina.setFechaRegistro(new Date());

        return cocinaRepo.save(ordenCocina);

    }

    // Metodo para actualizar el estado de la orden
    public OrdenCocina actualizarEstado(Integer id, String nuevoEstado){
        OrdenCocina ordenActualizada = cocinaRepo.findById(id).orElse(null);

        if(ordenActualizada != null){
            ordenActualizada.setEstado(nuevoEstado);
            return cocinaRepo.save(ordenActualizada);
        }
        return null;

    }

    


}
