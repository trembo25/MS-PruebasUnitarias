package cl.orderflowmanagement.gestionpedidos.controller;

import cl.orderflowmanagement.gestionpedidos.dto.*;
import cl.orderflowmanagement.gestionpedidos.model.Pedido;
import cl.orderflowmanagement.gestionpedidos.service.PedidoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PedidoController.class)
public class PedidoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PedidoService pedidoService;

    private Pedido pedidoEjemplo;

    @BeforeEach
    void setUp() {
        pedidoEjemplo = new Pedido(1, "pendiente", new Date(), 15000.0, 1, 5, new ArrayList<>());
    }

    // ==========================================
    // GET: LISTAR PEDIDOS
    // ==========================================
    @Test
    void listar_retorna200_cuandoRolEsPermitido() throws Exception {
        when(pedidoService.getPedidos()).thenReturn(Arrays.asList(pedidoEjemplo));
        mockMvc.perform(get("/v1/orderflow/gestionpedidos").header("X-Rol-Empleado", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    void listar_retorna403_cuandoRolEsDenegado() throws Exception {
        mockMvc.perform(get("/v1/orderflow/gestionpedidos").header("X-Rol-Empleado", "3"))
                .andExpect(status().isForbidden());
    }

    @Test
    void listar_retorna500_cuandoServicioFalla() throws Exception {
        when(pedidoService.getPedidos()).thenThrow(new RuntimeException("Error BD"));
        mockMvc.perform(get("/v1/orderflow/gestionpedidos").header("X-Rol-Empleado", "1"))
                .andExpect(status().isInternalServerError());
    }

    // ==========================================
    // POST: CREAR PEDIDO
    // ==========================================
    @Test
    void crearPedido_retorna201_exitoso() throws Exception {
        PedidoRequestDTO req = new PedidoRequestDTO(); req.setMesaId(5); req.setEmpleadoId(1);
        when(pedidoService.createPedido(5, 1)).thenReturn(pedidoEjemplo);
        mockMvc.perform(post("/v1/orderflow/gestionpedidos").header("X-Rol-Empleado", "1")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void crearPedido_retorna400_cuandoValidacionFalla() throws Exception {
        PedidoRequestDTO req = new PedidoRequestDTO(); req.setMesaId(5); req.setEmpleadoId(1);
        when(pedidoService.createPedido(5, 1)).thenReturn(null);
        mockMvc.perform(post("/v1/orderflow/gestionpedidos").header("X-Rol-Empleado", "2")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void crearPedido_retorna403_cuandoRolEsDenegado() throws Exception {
        mockMvc.perform(post("/v1/orderflow/gestionpedidos").header("X-Rol-Empleado", "3")
                .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void crearPedido_retorna500_cuandoServicioFalla() throws Exception {
        PedidoRequestDTO req = new PedidoRequestDTO(); req.setMesaId(5); req.setEmpleadoId(1);
        when(pedidoService.createPedido(5, 1)).thenThrow(new RuntimeException("Error general"));
        mockMvc.perform(post("/v1/orderflow/gestionpedidos").header("X-Rol-Empleado", "1")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isInternalServerError());
    }

    // ==========================================
    // POST: AÑADIR DETALLE
    // ==========================================
    @Test
    void aniadirDetalle_retorna200_exitoso() throws Exception {
        DetalleRequestDTO req = new DetalleRequestDTO(15, 2);
        when(pedidoService.addProducto(1, 15, 2)).thenReturn(pedidoEjemplo);
        mockMvc.perform(post("/v1/orderflow/gestionpedidos/1/detalles").header("X-Rol-Empleado", "2")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    void aniadirDetalle_retorna404_cuandoFallaValidacion() throws Exception {
        DetalleRequestDTO req = new DetalleRequestDTO(15, 2);
        when(pedidoService.addProducto(1, 15, 2)).thenReturn(null);
        mockMvc.perform(post("/v1/orderflow/gestionpedidos/1/detalles").header("X-Rol-Empleado", "2")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound());
    }

    @Test
    void aniadirDetalle_retorna403_cuandoRolEsDenegado() throws Exception {
        mockMvc.perform(post("/v1/orderflow/gestionpedidos/1/detalles").header("X-Rol-Empleado", "4")
                .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void aniadirDetalle_retorna500_cuandoServicioFalla() throws Exception {
        DetalleRequestDTO req = new DetalleRequestDTO(15, 2);
        when(pedidoService.addProducto(1, 15, 2)).thenThrow(new RuntimeException("Error"));
        mockMvc.perform(post("/v1/orderflow/gestionpedidos/1/detalles").header("X-Rol-Empleado", "1")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isInternalServerError());
    }

    // ==========================================
    // DELETE: ELIMINAR DETALLE
    // ==========================================
    @Test
    void eliminarDetalle_retorna200_exitoso() throws Exception {
        when(pedidoService.deleteProducto(1, 10)).thenReturn(pedidoEjemplo);
        mockMvc.perform(delete("/v1/orderflow/gestionpedidos/1/detalles/10").header("X-Rol-Empleado", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void eliminarDetalle_retorna404_cuandoNoExiste() throws Exception {
        when(pedidoService.deleteProducto(1, 10)).thenReturn(null);
        mockMvc.perform(delete("/v1/orderflow/gestionpedidos/1/detalles/10").header("X-Rol-Empleado", "2"))
                .andExpect(status().isNotFound());
    }

    @Test
    void eliminarDetalle_retorna403_cuandoRolEsDenegado() throws Exception {
        mockMvc.perform(delete("/v1/orderflow/gestionpedidos/1/detalles/10").header("X-Rol-Empleado", "3"))
                .andExpect(status().isForbidden());
    }

    @Test
    void eliminarDetalle_retorna500_cuandoServicioFalla() throws Exception {
        when(pedidoService.deleteProducto(1, 10)).thenThrow(new RuntimeException("Error"));
        mockMvc.perform(delete("/v1/orderflow/gestionpedidos/1/detalles/10").header("X-Rol-Empleado", "1"))
                .andExpect(status().isInternalServerError());
    }

    // ==========================================
    // PUT: ACTUALIZAR CANTIDAD DETALLE
    // ==========================================
    @Test
    void actualizarDetalle_retorna200_exitoso() throws Exception {
        CantidadRequestDTO req = new CantidadRequestDTO(); req.setCantidad(5);
        when(pedidoService.updateCantidad(1, 10, 5)).thenReturn(pedidoEjemplo);
        mockMvc.perform(put("/v1/orderflow/gestionpedidos/1/detalles/10").header("X-Rol-Empleado", "2")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    void actualizarDetalle_retorna404_cuandoNoExiste() throws Exception {
        CantidadRequestDTO req = new CantidadRequestDTO(); req.setCantidad(5);
        when(pedidoService.updateCantidad(1, 10, 5)).thenReturn(null);
        mockMvc.perform(put("/v1/orderflow/gestionpedidos/1/detalles/10").header("X-Rol-Empleado", "2")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound());
    }

    @Test
    void actualizarDetalle_retorna403_cuandoRolEsDenegado() throws Exception {
        mockMvc.perform(put("/v1/orderflow/gestionpedidos/1/detalles/10").header("X-Rol-Empleado", "3")
                .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void actualizarDetalle_retorna500_cuandoServicioFalla() throws Exception {
        CantidadRequestDTO req = new CantidadRequestDTO(); req.setCantidad(5);
        when(pedidoService.updateCantidad(1, 10, 5)).thenThrow(new RuntimeException("Error"));
        mockMvc.perform(put("/v1/orderflow/gestionpedidos/1/detalles/10").header("X-Rol-Empleado", "1")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isInternalServerError());
    }

    // ==========================================
    // PUT: PAGAR PEDIDO
    // ==========================================
    @Test
    void pagar_retorna200_exitoso() throws Exception {
        MetodoPagoRequestDTO req = new MetodoPagoRequestDTO(); req.setMetodoPago("debito");
        pedidoEjemplo.setEstado("pagado");
        when(pedidoService.payPedido(1, "debito")).thenReturn(pedidoEjemplo);
        mockMvc.perform(put("/v1/orderflow/gestionpedidos/1/pagar").header("X-Rol-Empleado", "1")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("pagado"));
    }

    @Test
    void pagar_retorna404_cuandoServicioFalla() throws Exception {
        MetodoPagoRequestDTO req = new MetodoPagoRequestDTO(); req.setMetodoPago("debito");
        when(pedidoService.payPedido(1, "debito")).thenReturn(null);
        mockMvc.perform(put("/v1/orderflow/gestionpedidos/1/pagar").header("X-Rol-Empleado", "2")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound());
    }

    @Test
    void pagar_retorna403_cuandoRolEsDenegado() throws Exception {
        mockMvc.perform(put("/v1/orderflow/gestionpedidos/1/pagar").header("X-Rol-Empleado", "3")
                .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void pagar_retorna500_cuandoServicioFallaExcepcion() throws Exception {
        MetodoPagoRequestDTO req = new MetodoPagoRequestDTO(); req.setMetodoPago("debito");
        when(pedidoService.payPedido(1, "debito")).thenThrow(new RuntimeException("Error general"));
        mockMvc.perform(put("/v1/orderflow/gestionpedidos/1/pagar").header("X-Rol-Empleado", "1")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isInternalServerError());
    }

    // ==========================================
    // GET: REPORTES COMPLETADOS
    // ==========================================
    @Test
    void obtenerPedidosCompletados_retorna200_exitoso() throws Exception {
        when(pedidoService.getPedidosPagadosParaReporte()).thenReturn(new ArrayList<>());
        mockMvc.perform(get("/v1/orderflow/gestionpedidos/completados").header("X-Rol-Empleado", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void obtenerPedidosCompletados_retorna403_cuandoNoEsAdmin() throws Exception {
        mockMvc.perform(get("/v1/orderflow/gestionpedidos/completados").header("X-Rol-Empleado", "2"))
                .andExpect(status().isForbidden());
    }

    @Test
    void obtenerPedidosCompletados_retorna500_cuandoServicioFalla() throws Exception {
        when(pedidoService.getPedidosPagadosParaReporte()).thenThrow(new RuntimeException("Error BD"));
        mockMvc.perform(get("/v1/orderflow/gestionpedidos/completados").header("X-Rol-Empleado", "1"))
                .andExpect(status().isInternalServerError());
    }

    // ==========================================
    // GET: REPORTES DETALLES TOTALES
    // ==========================================
    @Test
    void obtenerDetallesVentas_retorna200_exitoso() throws Exception {
        when(pedidoService.obtenerTodosLosDetallesParaReporte()).thenReturn(new ArrayList<>());
        mockMvc.perform(get("/v1/orderflow/gestionpedidos/detalles-totales").header("X-Rol-Empleado", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void obtenerDetallesVentas_retorna403_cuandoNoEsAdmin() throws Exception {
        mockMvc.perform(get("/v1/orderflow/gestionpedidos/detalles-totales").header("X-Rol-Empleado", "2"))
                .andExpect(status().isForbidden());
    }

    @Test
    void obtenerDetallesVentas_retorna500_cuandoServicioFalla() throws Exception {
        when(pedidoService.obtenerTodosLosDetallesParaReporte()).thenThrow(new RuntimeException("Error BD"));
        mockMvc.perform(get("/v1/orderflow/gestionpedidos/detalles-totales").header("X-Rol-Empleado", "1"))
                .andExpect(status().isInternalServerError());
    }
}