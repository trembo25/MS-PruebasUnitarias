package cl.ordery.inventario.inventario_api.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "gestionmenu", url = "http://localhost:8081/v1/orderflow/gestionmenu")
public interface MenuClient {

    // Método para verificar si el plato existe en el microservicio de pedido
    @GetMapping("/platos/{id}")
    Object buscarPlatoPorId(@PathVariable("id") Integer id);
}