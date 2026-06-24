package cl.orderflowmanagement.gestionpagosyfacturas.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.orderflowmanagement.gestionpagosyfacturas.dto.PagoRequestDTO;
import cl.orderflowmanagement.gestionpagosyfacturas.dto.PagoResponseDTO;
import cl.orderflowmanagement.gestionpagosyfacturas.model.Pago;
import cl.orderflowmanagement.gestionpagosyfacturas.repository.PagoRepository;
@Service
public class PagoService {

    @Autowired
    private PagoRepository pagoRepository;

    public PagoResponseDTO generarBoleta(PagoRequestDTO request) {
        Pago nuevoPago = new Pago();
        nuevoPago.setPedidoId(request.getPedidoId());
        nuevoPago.setTotalPagado(request.getTotal());
        
        // calcula neto e IVA
        Double neto = request.getTotal() / 1.19;
        Double iva = request.getTotal() - neto;
        
        // Redondeamos
        nuevoPago.setMontoNeto(Math.round(neto * 100.0) / 100.0);
        nuevoPago.setIva(Math.round(iva * 100.0) / 100.0);
        
        nuevoPago.setFechaHora(new Date());
        nuevoPago.setMetodoPago(request.getMetodoPago()); // harcodeado

        // Guardamos en la BD de Pagos
        Pago pagoGuardado = pagoRepository.save(nuevoPago);

        // Devolvemos tu recibo esperado
        return new PagoResponseDTO(
                "APROBADO",
                pagoGuardado.getId(), // Usamos el ID como nroBoleta
                pagoGuardado.getFechaHora(),
                pagoGuardado.getIva(),
                pagoGuardado.getTotalPagado()
        );
    }

    public PagoResponseDTO obtenerBoletaPorIdPedido(Integer pedidoId) {
        Pago pago = pagoRepository.findByPedidoId(pedidoId).orElse(null);

        if (pago == null) {
            return null;
        }

        return new PagoResponseDTO("APROBADO", pago.getId(), pago.getFechaHora(), pago.getIva(), pago.getTotalPagado());
    }
}
