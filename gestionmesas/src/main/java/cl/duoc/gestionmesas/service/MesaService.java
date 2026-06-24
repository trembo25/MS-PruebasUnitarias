package cl.duoc.gestionmesas.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.duoc.gestionmesas.model.Mesa;
import cl.duoc.gestionmesas.repository.MesaRepository;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class MesaService {

    @Autowired
    private MesaRepository mesaRepository;

    public List<Mesa> listarMesas() {
        return mesaRepository.findAll();
    } 

    public Mesa guardarMesa(Mesa mesa) {
        return mesaRepository.save(mesa);
    }

    public void eliminarMesa(Integer id) {
        if (mesaRepository.existsById(id)) {
            mesaRepository.deleteById(id);
        } else {
            throw new RuntimeException("Mesa no encontrada");
        }
    }

    // SOLO CAMBIAR ESTADO?
    public Mesa actualizarMesa(Integer id, String estadoMesa) {
        Mesa mesa = mesaRepository.findById(id).orElseThrow(() -> new RuntimeException("Mesa no encontrada"));

        if (estadoMesa.equalsIgnoreCase("disponible") || 
            estadoMesa.equalsIgnoreCase("ocupada") || 
            estadoMesa.equalsIgnoreCase("reservada") || 
            estadoMesa.equalsIgnoreCase("deshabilitada")) {
            mesa.setEstado(estadoMesa);
            return mesaRepository.save(mesa);
        } else {
            throw new IllegalArgumentException("Ese estado es invalido para la mesa.");
        }
    }

    public Mesa buscarMesaPorId(Integer id) {
        return mesaRepository.findById(id).orElseThrow(() -> new RuntimeException("Mesa no existe"));
    }
}
