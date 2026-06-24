package cl.orderflowmanagement.gestionpedidos.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import cl.orderflowmanagement.gestionpedidos.dto.PagoRequestDTO;
import cl.orderflowmanagement.gestionpedidos.dto.PagoResponseDTO;

// Decirle al Gabriel que esta tiene que ser la ruta
@FeignClient(name = "gestionpagos", url = "http://localhost:8087/v1/orderflow/gestionpagos")
public interface FacturacionClient {

    @PostMapping("/hacer-pago") // Esto tiene que coincidir con el endpoint para recibir pagos
    PagoResponseDTO enviarPago(@RequestBody PagoRequestDTO pedido);
}

