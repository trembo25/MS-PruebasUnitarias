package cl.orderflowmanagement.gestionproveedores.service;

import cl.orderflowmanagement.gestionproveedores.dto.FacturaDTO;
import cl.orderflowmanagement.gestionproveedores.dto.ProveedorDTO;
import cl.orderflowmanagement.gestionproveedores.model.Factura;
import cl.orderflowmanagement.gestionproveedores.model.Proveedor;
import cl.orderflowmanagement.gestionproveedores.repository.FacturaRepository;
import cl.orderflowmanagement.gestionproveedores.repository.ProveedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProveedorService {

    @Autowired
    private ProveedorRepository proveedorRepo;

    @Autowired
    private FacturaRepository facturaRepo;

    public List<ProveedorDTO> listarTodos() {
        List<Proveedor> proveedores = proveedorRepo.findAll();
        List<ProveedorDTO> listaDtos = new ArrayList<>();
        
        for (Proveedor p : proveedores) {
            listaDtos.add(convertirADTO(p));
        }
        return listaDtos;
    }

    public ProveedorDTO buscarPorId(Integer id) {
        Proveedor proveedor = proveedorRepo.findById(id).orElseThrow(()-> new RuntimeException("Proveedor no encontrado"));
            return convertirADTO(proveedor);
       
    }

    public ProveedorDTO guardarProveedor(Proveedor proveedor) {
        if (proveedor.getFacturas() == null) {
            proveedor.setFacturas(new ArrayList<>());
        }

        Proveedor proveedorGuardado = proveedorRepo.save(proveedor);
        return convertirADTO(proveedorGuardado);
    }

    public FacturaDTO guardarFactura(Factura factura) {
        Factura facturaGuardada = facturaRepo.save(factura);

        return new FacturaDTO(
            facturaGuardada.getId(),
            facturaGuardada.getNumeroFactura(),
            facturaGuardada.getMontoTotal(),
            facturaGuardada.getEstadoPedido(),
            facturaGuardada.getFechaEmision()
        );
    }

    public void eliminarProveedor(Integer id) {
        proveedorRepo.deleteById(id);
    }

    private ProveedorDTO convertirADTO(Proveedor p) {
        List<FacturaDTO> facturasDTO = new ArrayList<>();
        
        if (p.getFacturas() != null) {
            for (Factura f : p.getFacturas()) {
                FacturaDTO fDto = new FacturaDTO(f.getId(), f.getNumeroFactura(), f.getMontoTotal(), f.getEstadoPedido(),f.getFechaEmision());
                facturasDTO.add(fDto);
            }
        }
        return new ProveedorDTO(p.getId(), p.getRut(), p.getNombre(), p.getCorreo(), facturasDTO);
    }

    public FacturaDTO actualizarEstadoFactura(Integer facturaId, String nuevoEstado) {
        Factura factura = facturaRepo.findById(facturaId).orElse(null);

        if (factura == null || nuevoEstado == null || nuevoEstado.trim().isEmpty()) {
            return null;
        }

        String estadoFormeateado = nuevoEstado.trim().toUpperCase();
        List<String> estadosValidos = List.of("PENDIENTE", "EN CAMINO", "COMPLETADO", "CANCELADO");

        if (!estadosValidos.contains(estadoFormeateado)) {
            throw new IllegalArgumentException("Estado no permitido: "+nuevoEstado);
        }

        factura.setEstadoPedido(estadoFormeateado);
        Factura facturaGuardada = facturaRepo.save(factura);

        return new FacturaDTO(facturaGuardada.getId(), facturaGuardada.getNumeroFactura(), facturaGuardada.getMontoTotal(), facturaGuardada.getEstadoPedido(), facturaGuardada.getFechaEmision());

    }

    // DEJO ESTE METODO AQUI PARA DEJARLO MAS SIMPLE 
    // MÉTODO PARA EL MS DE REPORTES
    public List<FacturaDTO> listarTodasLasFacturas() {
        List<Factura> facturas = facturaRepo.findAll();
        List<FacturaDTO> listaDtos = new ArrayList<>();
        
        for (Factura f : facturas) {
            listaDtos.add(new FacturaDTO(
                f.getId(), 
                f.getNumeroFactura(), 
                f.getMontoTotal(), 
                f.getEstadoPedido(), 
                f.getFechaEmision()
            ));
        }
        return listaDtos;
    }
}