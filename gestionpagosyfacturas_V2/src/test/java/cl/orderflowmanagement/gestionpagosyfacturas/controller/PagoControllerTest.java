package cl.orderflowmanagement.gestionpagosyfacturas.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
 
import java.util.Date;
 
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
 
import com.fasterxml.jackson.databind.ObjectMapper;
 
import cl.orderflowmanagement.gestionpagosyfacturas.dto.PagoRequestDTO;
import cl.orderflowmanagement.gestionpagosyfacturas.dto.PagoResponseDTO;
import cl.orderflowmanagement.gestionpagosyfacturas.service.PagoService;


@WebMvcTest(PagosController.class)
public class PagoControllerTest {
 
    @Autowired
    private MockMvc mockMvc;
 
    @MockBean
    private PagoService pagoService;
 
    @Autowired
    private ObjectMapper objectMapper;
 
    private PagoRequestDTO pagoRequestEjemplo;
    private PagoResponseDTO pagoResponseEjemplo;
 
    @BeforeEach
    void setUp() {
        // Request DTO
        pagoRequestEjemplo = new PagoRequestDTO();
        pagoRequestEjemplo.setPedidoId(450);
        pagoRequestEjemplo.setTotal(119000.0);
        pagoRequestEjemplo.setMetodoPago("tarjeta_debito");
 
        // Response DTO
        pagoResponseEjemplo = new PagoResponseDTO();
        pagoResponseEjemplo.setEstado("APROBADO");
        pagoResponseEjemplo.setNroBoleta(1);
        pagoResponseEjemplo.setFechaHora(new Date());
        pagoResponseEjemplo.setIva(19000.0);
        pagoResponseEjemplo.setTotalPagado(119000.0);
    }
 
  
 
    @Test
    void cobrarPedido_retorna200_ConDatosValidos() throws Exception {
        // ARRANGE
        when(pagoService.generarBoleta(any(PagoRequestDTO.class))).thenReturn(pagoResponseEjemplo);
 
        // ACT & ASSERT
        mockMvc.perform(post("/v1/orderflow/gestionpagos/hacer-pago")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pagoRequestEjemplo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("APROBADO"))
                .andExpect(jsonPath("$.nroBoleta").value(1))
                .andExpect(jsonPath("$.totalPagado").value(119000.0));
    }
 
    @Test
    void cobrarPedido_retorna400_CuandoFaltaPedidoId() throws Exception {
        // ARRANGE - Request sin pedidoId
        pagoRequestEjemplo.setPedidoId(null);
 
        // ACT & ASSERT
        mockMvc.perform(post("/v1/orderflow/gestionpagos/hacer-pago")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pagoRequestEjemplo)))
                .andExpect(status().isBadRequest());
    }
 
    @Test
    void cobrarPedido_retorna400_CuandoTotalEsNegativo() throws Exception {
        // ARRANGE - Total negativo
        pagoRequestEjemplo.setTotal(-5000.0);
 
        // ACT & ASSERT
        mockMvc.perform(post("/v1/orderflow/gestionpagos/hacer-pago")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pagoRequestEjemplo)))
                .andExpect(status().isBadRequest());
    }
 
    @Test
    void cobrarPedido_retorna400_CuandoFaltaMetodoPago() throws Exception {
        // ARRANGE - sin método de pago
        pagoRequestEjemplo.setMetodoPago("");
 
        // ACT & ASSERT
        mockMvc.perform(post("/v1/orderflow/gestionpagos/hacer-pago")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pagoRequestEjemplo)))
                .andExpect(status().isBadRequest());
    }
 
    @Test
    void cobrarPedido_retorna500_CuandoServiceLanzaExcepcion() throws Exception {
        // ARRANGE
        when(pagoService.generarBoleta(any(PagoRequestDTO.class)))
                .thenThrow(new RuntimeException("Error al guardar pago"));
 
        // ACT & ASSERT
        mockMvc.perform(post("/v1/orderflow/gestionpagos/hacer-pago")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pagoRequestEjemplo)))
                .andExpect(status().isInternalServerError());
    }
 
   
 
    @Test
    void verBoleta_retorna200_ConRolValidoYBoletaExistente() throws Exception {
        // ARRANGE
        when(pagoService.obtenerBoletaPorIdPedido(450)).thenReturn(pagoResponseEjemplo);
 
        // ACT & ASSERT
        mockMvc.perform(get("/v1/orderflow/gestionpagos/boleta/450")
                .header("X-Rol-Empleado", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("APROBADO"))
                .andExpect(jsonPath("$.nroBoleta").value(1));
    }
 
    @Test
    void verBoleta_retorna200_ConRol2() throws Exception {
        // ARRANGE - Rol 2 también es válido
        when(pagoService.obtenerBoletaPorIdPedido(450)).thenReturn(pagoResponseEjemplo);
 
        // ACT & ASSERT
        mockMvc.perform(get("/v1/orderflow/gestionpagos/boleta/450")
                .header("X-Rol-Empleado", "2"))
                .andExpect(status().isOk());
    }
 
    @Test
    void verBoleta_retorna403_ConRolInvalido() throws Exception {
        // ARRANGE - Rol 3 no es válido
        when(pagoService.obtenerBoletaPorIdPedido(450)).thenReturn(pagoResponseEjemplo);
 
        // ACT & ASSERT
        mockMvc.perform(get("/v1/orderflow/gestionpagos/boleta/450")
                .header("X-Rol-Empleado", "3"))
                .andExpect(status().isForbidden());
    }
 
    @Test
    void verBoleta_retorna404_CuandoBoletaNoExiste() throws Exception {
        // ARRANGE - Service retorna null
        when(pagoService.obtenerBoletaPorIdPedido(999)).thenReturn(null);
 
        // ACT & ASSERT
        mockMvc.perform(get("/v1/orderflow/gestionpagos/boleta/999")
                .header("X-Rol-Empleado", "1"))
                .andExpect(status().isNotFound());
    }
 
    @Test
    void verBoleta_retorna500_CuandoServiceLanzaExcepcion() throws Exception {
        // ARRANGE
        when(pagoService.obtenerBoletaPorIdPedido(450))
                .thenThrow(new RuntimeException("Error en BD"));
 
        // ACT & ASSERT
        mockMvc.perform(get("/v1/orderflow/gestionpagos/boleta/450")
                .header("X-Rol-Empleado", "1"))
                .andExpect(status().isInternalServerError());
    }
}
