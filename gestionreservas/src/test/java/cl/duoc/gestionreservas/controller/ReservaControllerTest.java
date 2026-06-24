package cl.duoc.gestionreservas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import cl.duoc.gestionreservas.model.Reserva;
import cl.duoc.gestionreservas.service.ReservaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReservaController.class)
public class ReservaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReservaService reservaService;

    private Reserva reservaEjemplo;

    @BeforeEach
    void setUp() {
        reservaEjemplo = new Reserva();
        reservaEjemplo.setId(1);
        reservaEjemplo.setNombre("Juan Alberto");
        reservaEjemplo.setApellido("Pérez");
        reservaEjemplo.setRut("12.345.675-2");
        reservaEjemplo.setCantidadPersonas(4);
        reservaEjemplo.setFechaReserva(new Date());
        reservaEjemplo.setMesaId(1);
        reservaEjemplo.setEmpleadoId(2);
    }

    // ==========================================
    // TESTS PARA GET (LISTAR TODAS)
    // ==========================================

    @Test
    void listar_retorna200_cuandoRolEsPermitido() throws Exception {
        when(reservaService.listarReservas()).thenReturn(Arrays.asList(reservaEjemplo));

        mockMvc.perform(get("/v1/orderflow/gestionreservas")
                .header("X-Rol-Empleado", "4")) // Rol 4: Recepción
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Juan Alberto"));
    }

    @Test
    void listar_retorna403_cuandoRolNoTienePermisos() throws Exception {
        mockMvc.perform(get("/v1/orderflow/gestionreservas")
                .header("X-Rol-Empleado", "3")) // Cocina no puede ver esto
                .andExpect(status().isForbidden());
    }

    @Test
    void listar_retorna500_cuandoFallaElServidor() throws Exception {
        when(reservaService.listarReservas()).thenThrow(new RuntimeException("Error BD"));

        mockMvc.perform(get("/v1/orderflow/gestionreservas")
                .header("X-Rol-Empleado", "1"))
                .andExpect(status().isInternalServerError());
    }

    // ==========================================
    // TESTS PARA GET BY ID
    // ==========================================

    @Test
    void buscarPorId_retorna200_cuandoExiste() throws Exception {
        when(reservaService.buscarPorId(1)).thenReturn(reservaEjemplo);

        mockMvc.perform(get("/v1/orderflow/gestionreservas/id/1")
                .header("X-Rol-Empleado", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rut").value("12.345.675-2"));
    }

    @Test
    void buscarPorId_retorna404_cuandoNoExiste() throws Exception {
        when(reservaService.buscarPorId(99)).thenThrow(new RuntimeException("Reserva no encontrada"));

        mockMvc.perform(get("/v1/orderflow/gestionreservas/id/99")
                .header("X-Rol-Empleado", "1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void buscarPorId_retorna403_cuandoRolNoTienePermisos() throws Exception {
        mockMvc.perform(get("/v1/orderflow/gestionreservas/id/1")
                .header("X-Rol-Empleado", "2")) // Garzón no puede buscar reservas
                .andExpect(status().isForbidden());
    }

    // ==========================================
    // TESTS PARA POST (GUARDAR)
    // ==========================================

    @Test
    void guardar_retorna201_cuandoSeCreaExitosamente() throws Exception {
        when(reservaService.guardarReserva(any(Reserva.class))).thenReturn(reservaEjemplo);

        mockMvc.perform(post("/v1/orderflow/gestionreservas")
                .header("X-Rol-Empleado", "1") // Rol 1: Admin
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reservaEjemplo)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void guardar_retorna400_cuandoFallaValidacionDeMesaOEmpleado() throws Exception {
        when(reservaService.guardarReserva(any(Reserva.class)))
                .thenThrow(new IllegalArgumentException("La mesa no está disponible."));

        mockMvc.perform(post("/v1/orderflow/gestionreservas")
                .header("X-Rol-Empleado", "4")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reservaEjemplo)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void guardar_retorna403_cuandoRolNoTienePermisos() throws Exception {
        mockMvc.perform(post("/v1/orderflow/gestionreservas")
                .header("X-Rol-Empleado", "2") // Garzón no guarda reservas
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reservaEjemplo)))
                .andExpect(status().isForbidden());
    }

    @Test
    void guardar_retorna500_cuandoFallaElServidor() throws Exception {
        when(reservaService.guardarReserva(any(Reserva.class))).thenThrow(new RuntimeException("Error general"));

        mockMvc.perform(post("/v1/orderflow/gestionreservas")
                .header("X-Rol-Empleado", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reservaEjemplo)))
                .andExpect(status().isInternalServerError());
    }

    // ==========================================
    // TESTS PARA PUT (ACTUALIZAR)
    // ==========================================

    @Test
    void actualizar_retorna200_cuandoSeActualizaExitosamente() throws Exception {
        when(reservaService.actualizarReserva(any(Reserva.class))).thenReturn(reservaEjemplo);

        mockMvc.perform(put("/v1/orderflow/gestionreservas")
                .header("X-Rol-Empleado", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reservaEjemplo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void actualizar_retorna500_cuandoFallaLaActualizacion() throws Exception {
        when(reservaService.actualizarReserva(any(Reserva.class))).thenThrow(new RuntimeException("Error general"));

        mockMvc.perform(put("/v1/orderflow/gestionreservas")
                .header("X-Rol-Empleado", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reservaEjemplo)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void actualizar_retorna403_cuandoRolNoTienePermisos() throws Exception {
        mockMvc.perform(put("/v1/orderflow/gestionreservas")
                .header("X-Rol-Empleado", "2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reservaEjemplo)))
                .andExpect(status().isForbidden());
    }

    // ==========================================
    // TESTS PARA DELETE (ELIMINAR)
    // ==========================================

    @Test
    void eliminar_retorna204_cuandoBorradoEsExitoso() throws Exception {
        doNothing().when(reservaService).eliminarReserva(1);

        mockMvc.perform(delete("/v1/orderflow/gestionreservas/1")
                .header("X-Rol-Empleado", "4"))
                .andExpect(status().isNoContent());
    }

    @Test
    void eliminar_retorna404_cuandoNoExiste() throws Exception {
        doThrow(new RuntimeException("Reserva no encontrada")).when(reservaService).eliminarReserva(99);

        mockMvc.perform(delete("/v1/orderflow/gestionreservas/99")
                .header("X-Rol-Empleado", "1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void eliminar_retorna400_cuandoHayErrorDeArgumento() throws Exception {
        doThrow(new IllegalArgumentException("Error de validación")).when(reservaService).eliminarReserva(1);

        mockMvc.perform(delete("/v1/orderflow/gestionreservas/1")
                .header("X-Rol-Empleado", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void eliminar_retorna403_cuandoRolNoTienePermisos() throws Exception {
        mockMvc.perform(delete("/v1/orderflow/gestionreservas/1")
                .header("X-Rol-Empleado", "3"))
                .andExpect(status().isForbidden());
    }
}