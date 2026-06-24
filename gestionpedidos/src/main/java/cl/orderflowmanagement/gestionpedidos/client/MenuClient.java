package cl.orderflowmanagement.gestionpedidos.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import cl.orderflowmanagement.gestionpedidos.dto.PlatoResponseDTO;

@FeignClient(name = "gestionmenu", url = "http://localhost:8081/v1/orderflow/gestionmenu/platos")
public interface MenuClient {

    @GetMapping("/{id}")
    PlatoResponseDTO obtenerPlatoPorId(@PathVariable("id") Integer id);
}
