package cl.ordery.cocina.gestioncocina.Controller;

import cl.ordery.cocina.gestioncocina.controller.OrdenCocinaController;
import cl.ordery.cocina.gestioncocina.model.OrdenCocina;
import cl.ordery.cocina.gestioncocina.service.OrdenCocinaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrdenCocinaController.class) // levanta solo la capa web, sin BD
public class OrdenCocinaControllerTest {

    @Autowired
    private MockMvc mockMvc; // simula las peticiones HTTP

    @MockBean
    private OrdenCocinaService service; // servicio falso

    private OrdenCocina ordenEjemplo;

    @BeforeEach
    void setUp() {
        ordenEjemplo = new OrdenCocina();
        ordenEjemplo.setId(1);
        ordenEjemplo.setPedidoId(10);
        ordenEjemplo.setProductoId(5);
        ordenEjemplo.setCantidad(2);
    }

    // --- TEST GET ---
    @Test
    void listar_retorna200ConRolValido() throws Exception {
        // ARRANGE: creamos la lista falsa
        List<OrdenCocina> listaFalsa = new ArrayList<>();
        listaFalsa.add(ordenEjemplo);

        // le decimos al servicio falso que devuelva esa lista
        when(service.listarOrdenes()).thenReturn(listaFalsa);

        // ACT + ASSERT: simulamos el GET y verificamos la respuesta
        mockMvc.perform(get("/v1/orderflow/gestioncocina")
                .header("X-Rol-Empleado", "3")) // Chef sí puede ver
                .andExpect(status().isOk())                            // código HTTP 200
                .andExpect(jsonPath("$[0].pedidoId").value(10));       // primer elemento de la lista
    }

    @Test
    void listar_retorna403SinRolValido() throws Exception {
        // ACT + ASSERT: verificamos el bloqueo por rol
        mockMvc.perform(get("/v1/orderflow/gestioncocina")
                .header("X-Rol-Empleado", "2")) // Garzón no puede ver la fila completa
                .andExpect(status().isForbidden());                    // código HTTP 403
    }

    // --- TEST POST  ---
    @Test
    void crear_retorna201ConRolValido() throws Exception {
        // ARRANGE
        when(service.guardarOrden(any(OrdenCocina.class))).thenReturn(ordenEjemplo);

        // ACT + ASSERT
        mockMvc.perform(post("/v1/orderflow/gestioncocina")
                .header("X-Rol-Empleado", "2") // Garzón sí puede crear
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"pedidoId\":10, \"productoId\":5, \"cantidad\":2, \"estado\":\"Pendiente\"}"))
                .andExpect(status().isCreated())                       // código HTTP 201
                .andExpect(jsonPath("$.pedidoId").value(10));
    }

    @Test
    void crear_retorna403SinRolValido() throws Exception {
        // ACT + ASSERT: verificamos el bloqueo por rol
        mockMvc.perform(post("/v1/orderflow/gestioncocina")
                .header("X-Rol-Empleado", "3") // Chef no crea, solo prepara
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"pedidoId\":10, \"productoId\":5, \"cantidad\":2, \"estado\":\"Pendiente\"}"))
                .andExpect(status().isForbidden());                    // código HTTP 403
    }

    // --- TEST PUT  ---
    @Test
    void actualizarEstado_retorna200() throws Exception {
        // ARRANGE
        when(service.actualizarEstado(eq(1), any(String.class))).thenReturn(ordenEjemplo);

        // ACT + ASSERT
        mockMvc.perform(put("/v1/orderflow/gestioncocina/1/estado")
                .header("X-Rol-Empleado", "3") // Chef
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nuevoEstado\":\"Listo para servir\"}"))
                .andExpect(status().isOk());                           // código HTTP 200
    }

    @Test
    void actualizarEstado_retorna400CuerpoNull() throws Exception {
        // ACT + ASSERT: verificamos manejo de body inválido
        mockMvc.perform(put("/v1/orderflow/gestioncocina/1/estado")
                .header("X-Rol-Empleado", "3")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}")) //  Estdado null
                .andExpect(status().isBadRequest());                   // código HTTP 400
    }

    @Test
    void actualizarEstado_retorna404NoEncontrado() throws Exception {
        // ARRANGE: el servicio no encuentra el ID
        when(service.actualizarEstado(eq(99), any(String.class))).thenReturn(null);

        // ACT + ASSERT
        mockMvc.perform(put("/v1/orderflow/gestioncocina/99/estado")
                .header("X-Rol-Empleado", "1") // Admin
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nuevoEstado\":\"Listo para servir\"}"))
                .andExpect(status().isNotFound());                     // código HTTP 404
    }
}