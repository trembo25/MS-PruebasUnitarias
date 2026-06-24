package cl.orderflowmanagement.gestionreportes.service;

import cl.orderflowmanagement.gestionreportes.client.MenuClient;
import cl.orderflowmanagement.gestionreportes.client.ProveedorClient;
import cl.orderflowmanagement.gestionreportes.client.VentasClient;
import cl.orderflowmanagement.gestionreportes.dto.*;
import cl.orderflowmanagement.gestionreportes.model.Reporte;
import cl.orderflowmanagement.gestionreportes.repository.ReporteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ReporteService {

    @Autowired
    private ReporteRepository reporteRepo;

    @Autowired
    private VentasClient ventasClient;

    @Autowired
    private MenuClient menuClient;

    @Autowired
    private ProveedorClient proveedorClient;


    public Reporte generarReportePlatoGanador() {
        List<DetalleRequestDTO> detalles = ventasClient.obtenerDetallesVentas("1");
        
        if (detalles == null || detalles.isEmpty()) {
            throw new RuntimeException("No hay ventas registradas en el sistema.");
        }

        List<Integer> listaIds = new ArrayList<>();
        List<Integer> listaCantidades = new ArrayList<>();

        for (DetalleRequestDTO detalle : detalles) {
            int id = detalle.getProductoId();
            int cantidad = detalle.getCantidad();

            if (listaIds.contains(id)) {
                // Si el id ya está en la lista, le sumamos la cantidad
                int indice = listaIds.indexOf(id);
                int cantidadAcumulada = listaCantidades.get(indice);
                listaCantidades.set(indice, cantidadAcumulada + cantidad);
            } else {
                // Si es un id nuevo, lo agregamos
                listaIds.add(id);
                listaCantidades.add(cantidad);
            }
        }

        int maxCantidad = 0;
        int idGanador = 0;

        for (int i = 0; i < listaCantidades.size(); i++) {
            if (listaCantidades.get(i) > maxCantidad) {
                maxCantidad = listaCantidades.get(i);
                idGanador = listaIds.get(i);
            }
        }

        // Buscar el nombre del plato en el otro microservicio
        String nombrePlato = "Plato ID " + idGanador; // Por si el MS de menú está apagado
        try {
            PlatoResponseDTO plato = menuClient.obtenerPlatoPorId(idGanador);
            if (plato != null) {
                nombrePlato = plato.getNombre();
            }
        } catch (Exception e) {
            System.out.println("No se pudo conectar con el MS de Menú.");
        }

        // Guardar en Base de Datos
        Reporte reporte = new Reporte();
        reporte.setTipoReporte("RANKING_PLATOS");
        reporte.setFechaGeneracion(new Date());
        reporte.setMontoTotal((double) maxCantidad); // Usamos montoTotal para guardar la cantidad
        reporte.setResumen("Plato más vendido: " + nombrePlato + " con " + maxCantidad + " unidades.");

        return reporteRepo.save(reporte);
    }

    public Reporte generarReporteVentasMes(int mes, int anio) {
        List<PedidoReporteDTO> pedidos = ventasClient.obtenerPedidosCompletados("1");
        
        double sumaDinero = 0.0;
        int contadorPedidos = 0;

        if (pedidos != null) {
            for (PedidoReporteDTO pedido : pedidos) {
                // Validación de fechas súper extensa y legible
                if (pedido.getFechaHora() != null) {
                    
                    // getMonth() devuelve meses del 0 al 11, por eso sumamos 1
                    int mesDelPedido = pedido.getFechaHora().getMonth() + 1;
                    
                    // getYear() devuelve los años pasados desde 1900, por eso sumamos 1900
                    int anioDelPedido = pedido.getFechaHora().getYear() + 1900;

                    // Filtramos si coincide con lo que el usuario pidió
                    if (mesDelPedido == mes && anioDelPedido == anio) {
                        sumaDinero = sumaDinero + pedido.getTotal();
                        contadorPedidos++;
                    }
                }
            }
        }

        // Guardar en Base de Datos
        Reporte reporte = new Reporte();
        reporte.setTipoReporte("VENTAS_MENSUALES");
        reporte.setFechaGeneracion(new Date());
        reporte.setMontoTotal(sumaDinero);
        reporte.setResumen("Mes: " + mes + "/" + anio + ". Total de pedidos completados: " + contadorPedidos);

        return reporteRepo.save(reporte);
    }

    public Reporte generarReporteGastosMes(int mes, int anio) {
        List<FacturasDTO> facturas = proveedorClient.obtenerTodasLasFacturas("1");
        
        double sumaGastos = 0.0;
        int contadorFacturas = 0;

        if (facturas != null) {
            for (FacturasDTO factura : facturas) {
                // 1. FILTRAMOS SOLO LAS COMPLETADAS
                if (factura.getEstadoPedido() != null && factura.getEstadoPedido().equalsIgnoreCase("COMPLETADO")) {
                    
                    // 2. AHORA SÍ REVISAMOS LA FECHA
                    if (factura.getFechaEmision() != null) {
                        int mesFactura = factura.getFechaEmision().getMonth() + 1;
                        int anioFactura = factura.getFechaEmision().getYear() + 1900;

                        if (mesFactura == mes && anioFactura == anio) {
                            sumaGastos = sumaGastos + factura.getMontoTotal();
                            contadorFacturas++;
                        }
                    }
                }
            }
        }

        Reporte reporte = new Reporte();
        reporte.setTipoReporte("GASTOS_PROVEEDORES");
        reporte.setFechaGeneracion(new Date());
        reporte.setMontoTotal(sumaGastos);
        reporte.setResumen("Mes: " + mes + "/" + anio + ". Total de facturas pagadas: " + contadorFacturas);

        return reporteRepo.save(reporte);
    }

    public List<Reporte> listarHistorial() {
        return reporteRepo.findAll();
    }
}