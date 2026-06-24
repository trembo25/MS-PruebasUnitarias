package cl.orderflowmanagement.gestionreportes.client;

import cl.orderflowmanagement.gestionreportes.dto.PlatoResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "gestionmenu", url = "http://localhost:8081/v1/orderflow/gestionmenu")
public interface MenuClient {
    @GetMapping("/platos/{id}")
    PlatoResponseDTO obtenerPlatoPorId(@PathVariable("id") Integer id);
}
