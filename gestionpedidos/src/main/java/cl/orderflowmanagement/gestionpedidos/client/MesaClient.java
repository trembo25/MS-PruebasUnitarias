package cl.orderflowmanagement.gestionpedidos.client;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import cl.orderflowmanagement.gestionpedidos.dto.MesaResponseDTO;

@FeignClient(name = "mesa-client", url = "http://localhost:8082/v1/orderflow/gestionmesas")
public interface MesaClient {
    
    @GetMapping("/{id}")
    MesaResponseDTO obtenerMesaPorId(@PathVariable Integer id);

    @PutMapping("/{id}/estado")
    void actualizarEstadoMesa(@PathVariable Integer id, @RequestBody Map<String, String> estado);
}
