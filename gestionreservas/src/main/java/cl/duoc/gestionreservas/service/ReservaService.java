package cl.duoc.gestionreservas.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.duoc.gestionreservas.client.EmpleadoClient;
import cl.duoc.gestionreservas.client.MesaClient;
import cl.duoc.gestionreservas.dto.EmpleadoResponseDTO;
import cl.duoc.gestionreservas.dto.MesaResponseDTO;
import cl.duoc.gestionreservas.model.Reserva;
import cl.duoc.gestionreservas.repository.ReservaRepository;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private EmpleadoClient empleadoClient;

    @Autowired
    private MesaClient mesaClient;

    public List<Reserva> listarReservas() {
        return reservaRepository.findAll();
    }

    public Reserva guardarReserva(Reserva reserva) {
        /* VALIDACION EMPLEADO  */
        try {
            EmpleadoResponseDTO empleado = empleadoClient.obtenerEmpleadoPorId(reserva.getEmpleadoId());
            if (empleado == null) {
                throw new IllegalArgumentException("El empleado asignado no existe en el sistema.");
            }
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al validar empleado. " + e.getMessage());
        }

        /* VALIDACION MESA */
        try {
            MesaResponseDTO mesa = mesaClient.obtenerMesaPorId(reserva.getMesaId());
            
            if (mesa == null || !mesa.getEstado().equalsIgnoreCase("disponible")) {
                throw new IllegalArgumentException("La mesa " + reserva.getMesaId() + " no está disponible.");
            }

            if (mesa.getCapacidad() < reserva.getCantidadPersonas()) {
                throw new IllegalArgumentException("La mesa " + mesa.getId() + " no tiene capacidad suficiente. Capacidad máxima: " + mesa.getCapacidad());
            }
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al conectar con Gestión de Mesas.");
        }
         
        Reserva reservaGuardada = reservaRepository.save(reserva);

        try {
            Map<String, String> nuevoEstado = new HashMap<>();
            nuevoEstado.put("estado", "reservada");
            mesaClient.actualizarEstadoMesa(reserva.getMesaId(), nuevoEstado);
            System.out.println("Mesa: " + reserva.getMesaId() + " reservada con éxito.");
        } catch (Exception e) {
            System.out.println("Reserva guardada. Pero no se actualizó el estado.");
        }

        return reservaGuardada;
    }

    public void eliminarReserva(Integer id) {
        Reserva reserva = reservaRepository.findById(id).orElseThrow(() -> new RuntimeException("Reserva no encontrada."));
        reservaRepository.deleteById(id);   

        try {
            Map<String, String> nuevoEstado = new HashMap<>();
            nuevoEstado.put("estado", "disponible");

            mesaClient.actualizarEstadoMesa(reserva.getMesaId(), nuevoEstado);
            System.out.println("Mesa: "+reserva.getMesaId()+". Reserva eliminada. Mesa disponible.");
        } catch (Exception e) {
            System.out.println("Reserva eliminada. Pero no se actualizó el estado.");
        }
    }

    public Reserva actualizarReserva(Reserva nuevaReserva) {
        Reserva reservaAntigua = reservaRepository.findById(nuevaReserva.getId()).orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));

        // Aqui estoy tratando de verificar si se cambio de mesa, o cambiaron la cantidad de personas de la reserva. 
        if (!reservaAntigua.getMesaId().equals(nuevaReserva.getMesaId()) || !reservaAntigua.getCantidadPersonas().equals(nuevaReserva.getCantidadPersonas())) {
            try {
                MesaResponseDTO mesaNueva = mesaClient.obtenerMesaPorId(nuevaReserva.getMesaId());

                // revisar mesa disponible
                if (!reservaAntigua.getMesaId().equals(nuevaReserva.getMesaId()) && !mesaNueva.getEstado().equalsIgnoreCase("disponible")) {
                    throw new IllegalArgumentException("La nueva mesa no está disponible");
                }

                // revisar capacidad
                if (nuevaReserva.getCantidadPersonas() > mesaNueva.getCapacidad()) {
                    throw new IllegalArgumentException("La nueva mesa no tiene la capacidad suficiente. Nueva Mesa: "+mesaNueva.getCapacidad());
                }

            } catch (IllegalArgumentException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException("Error al validar la mesa nueva.");
            }
        }

        Integer reservaAntiguaId = reservaAntigua.getMesaId();
        Integer reservaNuevaId = nuevaReserva.getMesaId();


        reservaAntigua.setNombre(nuevaReserva.getNombre());
        reservaAntigua.setApellido(nuevaReserva.getApellido());
        reservaAntigua.setRut(nuevaReserva.getRut());
        reservaAntigua.setCantidadPersonas(nuevaReserva.getCantidadPersonas());
        reservaAntigua.setFechaReserva(nuevaReserva.getFechaReserva());
        reservaAntigua.setMesaId(nuevaReserva.getMesaId());

        Reserva reservaActualizada = reservaRepository.save(reservaAntigua);

        if (!reservaNuevaId.equals(reservaAntiguaId)) {
            try {
                // dejar disponible la mesa antigua
                Map<String, String> mesaAntiguaADisponible = new HashMap<>();
                mesaAntiguaADisponible.put("estado", "disponible");
                mesaClient.actualizarEstadoMesa(reservaAntiguaId, mesaAntiguaADisponible);

                // dejar reservada la nueva mesa
                Map<String, String> mesaNuevaAReservada = new HashMap<>();
                mesaNuevaAReservada.put("estado", "reservada");
                mesaClient.actualizarEstadoMesa(reservaNuevaId, mesaNuevaAReservada);

                System.out.println("La mesa antigua: "+reservaAntiguaId+" ahora está disponible. La mesa nueva: "+reservaNuevaId+" ahora está reservada");
            } catch (Exception e) {
                System.out.println("Se actualizó la reserva. Pero no se actualizaron el estado de las mesas");
            }
        }

        return reservaActualizada;
    }

    // METODO PARA OTROS MICROSERVICIOS
    public Reserva buscarPorId(Integer id) {
        return reservaRepository.findById(id).orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
    }
}
