package cl.orderflowmanagement.gestionpedidos.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import cl.orderflowmanagement.gestionpedidos.dto.EmpleadoResponseDTO;

@FeignClient(name = "empleado-client", url = "http://localhost:8080/v1/orderflow/gestionempleados")
public interface EmpleadoClient {

    @GetMapping("/{id}")
    EmpleadoResponseDTO obtenerEmpleadoPorId(@PathVariable Integer id);
}
