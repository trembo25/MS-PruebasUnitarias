package cl.orderflowmanagement.gestionpedidos.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import cl.orderflowmanagement.gestionpedidos.dto.OrdenCocinaDTO;

@FeignClient(name = "gestioncocina", url = "http://localhost:8084/v1/orderflow/gestioncocina")
public interface CocinaClient {
    @PostMapping
    Object enviarOrden(@RequestHeader("X-Rol-Empleado") String rol, @RequestBody OrdenCocinaDTO orden);
}
