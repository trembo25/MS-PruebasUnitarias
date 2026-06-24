package cl.orderflowmanagement.gestionreportes.client;

import cl.orderflowmanagement.gestionreportes.dto.FacturasDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import java.util.List;

@FeignClient(name = "gestionproveedores", url = "http://localhost:8086/v1/orderflow/gestionproveedores")
public interface ProveedorClient {
    
    @GetMapping("/facturas")
    List<FacturasDTO> obtenerTodasLasFacturas(@RequestHeader("X-Rol-Empleado") String rol);
}