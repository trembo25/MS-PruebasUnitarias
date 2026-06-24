package cl.orderflowmanagement.gestionpedidos.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.orderflowmanagement.gestionpedidos.client.CocinaClient;
import cl.orderflowmanagement.gestionpedidos.client.EmpleadoClient;
import cl.orderflowmanagement.gestionpedidos.client.FacturacionClient;
import cl.orderflowmanagement.gestionpedidos.client.MenuClient;
import cl.orderflowmanagement.gestionpedidos.client.MesaClient;
import cl.orderflowmanagement.gestionpedidos.dto.DetalleRequestDTO;
import cl.orderflowmanagement.gestionpedidos.dto.EmpleadoResponseDTO;
import cl.orderflowmanagement.gestionpedidos.dto.MesaResponseDTO;
import cl.orderflowmanagement.gestionpedidos.dto.OrdenCocinaDTO;
import cl.orderflowmanagement.gestionpedidos.dto.PagoRequestDTO;
import cl.orderflowmanagement.gestionpedidos.dto.PagoResponseDTO;
import cl.orderflowmanagement.gestionpedidos.dto.PedidoReporteDTO;
import cl.orderflowmanagement.gestionpedidos.dto.PlatoResponseDTO;
import cl.orderflowmanagement.gestionpedidos.model.DetallePedido;
import cl.orderflowmanagement.gestionpedidos.model.Pedido;
import cl.orderflowmanagement.gestionpedidos.repository.PedidoRepository;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class PedidoService {

    // LOS ID == ID PUEDEN CAUSAR ERROR, CAMBIAR POR EQUALS CUANDO ESTEN LOS OTROS MICROSERIVICOS LISTOS.

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private EmpleadoClient empleadoClient;

    @Autowired
    private MesaClient mesaClient;

    @Autowired
    private MenuClient menuClient;

    @Autowired
    private CocinaClient cocinaClient;

    @Autowired
    private FacturacionClient facturacionClient;

    public List<Pedido> getPedidos() {
        return pedidoRepository.findAll();
    }

    // ESTE ES EL METODO QUE VOY A ALTERAR PARA RECIBIR EL DTO DEL MICROSERVICIO DE EMPLEADOS
    public Pedido createPedido(Integer mesaId, Integer empleadoId) {

        /* VALIDACION DE GARZON */

        try {
            EmpleadoResponseDTO empleadoValidado = empleadoClient.obtenerEmpleadoPorId(empleadoId);

            if (empleadoValidado == null || empleadoValidado.getRolEmpleado() == null || (empleadoValidado.getRolEmpleado().getId() != 1 && empleadoValidado.getRolEmpleado().getId() != 2)) {
                System.out.println("El empleado no tiene permisos como Garzón o Administrador. No puede acceder a este apartado.");
                return null;
            }
            System.out.println("Validación exitosa. Garzón ID: "+empleadoValidado.getId());
        } catch (Exception e) {
            System.out.println("Error de conexión. No se pudo conectar con el microservico.");
            return null;
        }

        /* VALIDACION DE MESAS */
        
        try {
            MesaResponseDTO mesaValidada = mesaClient.obtenerMesaPorId(mesaId);

            if (mesaValidada == null || mesaValidada.getEstado() == null) {
                System.out.println("La mesa no tiene registrado un estado. Por tanto es invalida.");
                return null;
            }

            if (!mesaValidada.getEstado().equalsIgnoreCase("disponible") && !mesaValidada.getEstado().equalsIgnoreCase("reservada")) {
                System.out.println("La mesa: "+mesaId+" no está disponible.");
                return null;
            }
            System.out.println("Validación exitosa. Mesa ID: "+mesaValidada.getId());
        } catch (Exception e) {
            System.out.println("Error de conexión. No se pudo conectar con el microservico.");
            return null;
        }

        Pedido pedidoNuevo = new Pedido(null, "pendiente", (new Date()), 0.0, empleadoId, mesaId, (new ArrayList<>()));
        Pedido pedidoGuardado = pedidoRepository.save(pedidoNuevo);

        /* CAMBIAR ESTADO DE MESAS */
        try {
            Map<String, String> nuevoEstado = new HashMap<>();
            nuevoEstado.put("estado", "ocupada");

            mesaClient.actualizarEstadoMesa(mesaId, nuevoEstado);
            System.out.println("Mesa: "+mesaId+" cambió su estado a: ocupada");
        } catch (Exception e) {
            System.out.println("Pedido creado, pero la mesa no se pudo actualizar.");
        }

        return pedidoGuardado;
    }

    public Pedido addProducto(Integer pedidoId, Integer productoId, Integer cantidad) {
        Pedido pedido = pedidoRepository.findById(pedidoId).orElse(null);

        if (pedido == null || pedido.getEstado().equalsIgnoreCase("pagado")) {
            return null;
        }

        // MICROSERVICIO MENÚ DEL VICHO.
        PlatoResponseDTO plato;
        try {
            plato = menuClient.obtenerPlatoPorId(productoId);

            if (plato == null) {
                System.out.println("Error. El producto ID: "+productoId+" no existe en el menú");
                return null;
            }

            if (!plato.getDisponible()) {
                System.out.println("Error. El producto: "+plato.getNombre()+" no está disponible por el momento.");
                return null;
            }

        } catch (Exception e) {
            System.out.println("Error de conexión. No se pudo conectar con el microservico.");
            return null;
        }

        Double subtotal = cantidad * plato.getPrecio();// MICROSERVICIO VICHO

        DetallePedido detalle = new DetallePedido(null, cantidad, subtotal, productoId, pedido);

        pedido.getDetalles().add(detalle);

        pedido.setTotal(pedido.getTotal() + subtotal);

        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        // INTEGRACION AL MS DE COCINA (MS DE VICHO)
        try {
            OrdenCocinaDTO ordenCocina = new OrdenCocinaDTO(null, pedidoId, productoId, cantidad, "Pendiente");
            cocinaClient.enviarOrden("2", ordenCocina); // el garzon envia la orden, por eso sale rol "2"
            System.out.println("Orden: "+pedidoId+", enviada con exito al panel de cocina.");
        } catch (Exception e) {
            System.out.println("Error de conexión. No se pudo conectar con el microservico.");
        }

        return pedidoGuardado;
    }

    public Pedido deleteProducto(Integer pedidoId, Integer detalleId) {
        Pedido pedido = pedidoRepository.findById(pedidoId).orElse(null);

        if (pedido == null) {
            return null;
        }

        if (pedido.getEstado().equalsIgnoreCase("pagado")) {
            return null;
        }

        for (DetallePedido detalle : pedido.getDetalles()) {
            if (detalle.getId().equals(detalleId)) {
                Double subtotal = detalle.getSubtotal();
                pedido.setTotal(pedido.getTotal() - subtotal);
                break;
            }
        }

        pedido.getDetalles().removeIf(detalle -> detalle.getId().equals(detalleId));

        return pedidoRepository.save(pedido);
    }

    public Pedido updateCantidad(Integer pedidoId, Integer detalleId, Integer nuevaCantidad) {
        Pedido pedido = pedidoRepository.findById(pedidoId).orElse(null);

        if (pedido == null || pedido.getEstado().equalsIgnoreCase("pagado")) {
            return null;
        }

        for (DetallePedido detalle : pedido.getDetalles()) {
            if (detalle.getId().equals(detalleId)) {

                PlatoResponseDTO plato;
                try {
                    plato = menuClient.obtenerPlatoPorId(detalle.getProductoId());

                    if (plato == null) {
                        System.out.println("Error. No se encontró el plato");
                        return null;
                    }

                } catch (Exception e) {
                    System.out.println("Error de conexión. No se pudo conectar con el microservico.");
                    return null;
                }

                Double subtotal = detalle.getSubtotal();

                pedido.setTotal(pedido.getTotal() - subtotal);

                Double subtotalNuevo = nuevaCantidad * plato.getPrecio(); // MICROSERVICIO VICHO

                detalle.setCantidad(nuevaCantidad);
                detalle.setSubtotal(subtotalNuevo);

                pedido.setTotal(pedido.getTotal() + subtotalNuevo);
                break;
            }
        }

        return pedidoRepository.save(pedido);
    }

    public Pedido payPedido(Integer pedidoId, String metodoPago) {
        Pedido pedido = pedidoRepository.findById(pedidoId).orElse(null);

        if (pedido == null || pedido.getEstado().equalsIgnoreCase("pagado")) {
            return null;
        }

        // INTEGRACION DEL MICROSERVICIO DE PAGOS DEL GABRIEL
        try {
            PagoRequestDTO pago = new PagoRequestDTO(pedido.getId(), pedido.getTotal(), metodoPago);

            PagoResponseDTO boleta = facturacionClient.enviarPago(pago);

            if (boleta == null || !boleta.getEstado().equalsIgnoreCase("aprobado")) {
                System.out.println("El pago fue rechazado por el microservicio de pagos y facturacion.");
                return null;
            }

            System.out.println("El pago se realizó con exito. Boleta N°: "+boleta.getNroBoleta());
        } catch (Exception e) {
            System.out.println("Error de conexión. No se pudo conectar con el microservico.");
            return null;
        }

        pedido.setEstado("pagado");
        Pedido pedidoPagado = pedidoRepository.save(pedido);

        try {
            Map<String, String> nuevoEstado = new HashMap<>();
            nuevoEstado.put("estado", "disponible");

            mesaClient.actualizarEstadoMesa(pedido.getMesaId(), nuevoEstado);
            System.out.println("Mesa: "+pedido.getMesaId()+" cambió su estado a: disponible");
        } catch (Exception e) {
            System.out.println("Pedido pagado, pero la mesa no se pudo actualizar.");
        }

        return pedidoPagado;
    }

    // ESTO ES PARA EL MICROSERVICIO DE REPORTES.
    public List<PedidoReporteDTO> getPedidosPagadosParaReporte() {
        List<Pedido> todosLosPedidos = pedidoRepository.findAll();

        List<PedidoReporteDTO> listaParaReportes = new ArrayList<>();

        for (Pedido pedido : todosLosPedidos) {
            if (pedido.getEstado().equalsIgnoreCase("pagado")) {
                PedidoReporteDTO dto = new PedidoReporteDTO(pedido.getId(), pedido.getFechaHora(), pedido.getTotal());
                listaParaReportes.add(dto);
            }
        }
        return listaParaReportes;
    }

    // ESTO ES PARA EL MICROSERVICIO DE REPORTES. (Detalles individuales, raking platos.)
    public List<DetalleRequestDTO> obtenerTodosLosDetallesParaReporte() {
        List<Pedido> todosLosPedidos = pedidoRepository.findAll();
        List<DetalleRequestDTO> listaDetalles = new ArrayList<>();

        for (Pedido pedido : todosLosPedidos) {
            if (pedido.getEstado().equalsIgnoreCase("pagado")) {
                
                for (DetallePedido detalle : pedido.getDetalles()) { 
                    
                    DetalleRequestDTO dto = new DetalleRequestDTO(detalle.getProductoId(), detalle.getCantidad());
                    listaDetalles.add(dto);
                }
            }
        }
        return listaDetalles;
    }
}
