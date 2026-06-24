package cl.ordery.menu.menu_api.controller;

import cl.ordery.menu.menu_api.dto.PlatoDTO;
import cl.ordery.menu.menu_api.service.PlatoService;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PlatoController.class) // levanta solo la capa web, sin BD
public class PlatoControllerTest {

    @Autowired
    private MockMvc mockMvc; // simula las peticiones HTTP

    @MockBean
    private PlatoService service; // servicio falso

    private PlatoDTO platoEjemplo;

    @BeforeEach
    void setUp() {
        platoEjemplo = new PlatoDTO();
        platoEjemplo.setId(1);
        platoEjemplo.setNombre("Lomo a lo Pobre");
        platoEjemplo.setPrecio(12000.0);
        platoEjemplo.setDisponible(true);
    }

    @Test
    void listarTodos_retorna200ConPlatos() throws Exception {
        // ARRANGE: creamos la lista falsa
        List<PlatoDTO> listaFalsa = new ArrayList<>();
        listaFalsa.add(platoEjemplo);
        
        // le decimos al servicio falso que devuelva esa lista
        when(service.getPlatos()).thenReturn(listaFalsa);

        // ACT + ASSERT: simulamos el GET y verificamos la respuesta
        mockMvc.perform(get("/v1/orderflow/gestionmenu/platos")
                .header("X-Rol-Empleado", "1"))
                .andExpect(status().isOk())                            // código HTTP 200
                .andExpect(jsonPath("$[0].nombre").value("Lomo a lo Pobre"));
    }

    @Test
    void obtenerPorId_retorna200() throws Exception {
        // ARRANGE: el servicio devuelve el plato
        when(service.getPlatoPorId(1)).thenReturn(platoEjemplo);

        // ACT + ASSERT
        mockMvc.perform(get("/v1/orderflow/gestionmenu/platos/1"))
                .andExpect(status().isOk())                            // código HTTP 200
                .andExpect(jsonPath("precio").value(12000.0));
    }

    @Test
    void guardar_retorna201() throws Exception {
        // ARRANGE
        when(service.crearPlato(any(PlatoDTO.class))).thenReturn(platoEjemplo);

        // ACT + ASSERT
        mockMvc.perform(post("/v1/orderflow/gestionmenu/platos")
                .header("X-Rol-Empleado", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nombre\":\"Lomo a lo Pobre\", \"precio\":12000.0, \"disponible\":true, \"categoriaId\":1}"))
                .andExpect(status().isCreated());                      // código HTTP 201
    }

    @Test
    void cambiarEstado_retorna200() throws Exception {
        // ARRANGE
        when(service.actualizarPlato(1, false)).thenReturn(platoEjemplo);

        // ACT + ASSERT
        mockMvc.perform(put("/v1/orderflow/gestionmenu/platos/1/disponibilidad")
                .header("X-Rol-Empleado", "1")
                .param("disponible", "false")) 
                .andExpect(status().isOk());                           // código HTTP 200
    }

    @Test
    void eliminar_retorna204() throws Exception {
        // ARRANGE: verifica existencia y elimina
        when(service.getPlatoPorId(1)).thenReturn(platoEjemplo);
        doNothing().when(service).eliminarPlato(1);

        // ACT + ASSERT
        mockMvc.perform(delete("/v1/orderflow/gestionmenu/platos/1")
                .header("X-Rol-Empleado", "1"))
                .andExpect(status().isNoContent());                    // código HTTP 204
    }
}