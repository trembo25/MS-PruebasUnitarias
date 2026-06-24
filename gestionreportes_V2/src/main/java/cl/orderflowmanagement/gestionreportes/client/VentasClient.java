package cl.orderflowmanagement.gestionreportes.client;

import cl.orderflowmanagement.gestionreportes.dto.DetalleRequestDTO;
import cl.orderflowmanagement.gestionreportes.dto.PedidoReporteDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import java.util.List;

@FeignClient(name = "gestionpedidos", url = "http://localhost:8083/v1/orderflow/gestionpedidos")
public interface VentasClient {
    
    @GetMapping("/completados") 
    List<PedidoReporteDTO> obtenerPedidosCompletados(@RequestHeader("X-Rol-Empleado") String rol);

    @GetMapping("/detalles-totales")
    List<DetalleRequestDTO> obtenerDetallesVentas(@RequestHeader("X-Rol-Empleado") String rol);
}