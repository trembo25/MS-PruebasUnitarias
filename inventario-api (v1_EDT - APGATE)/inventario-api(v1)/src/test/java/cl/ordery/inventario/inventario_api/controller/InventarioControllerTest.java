package cl.ordery.inventario.inventario_api.controller;

import cl.ordery.inventario.inventario_api.dto.IngredienteDTO;
import cl.ordery.inventario.inventario_api.dto.RecetaDTO;
import cl.ordery.inventario.inventario_api.service.InventarioService;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InventarioController.class)
public class InventarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InventarioService service;

    private IngredienteDTO ingredienteEjemplo;
    private RecetaDTO recetaEjemplo;

    @BeforeEach
    void setUp() {
        ingredienteEjemplo = new IngredienteDTO();
        ingredienteEjemplo.setId(1);
        ingredienteEjemplo.setNombre("Tomate");
        ingredienteEjemplo.setStock(50.0);
        ingredienteEjemplo.setUnidad("kg");

        recetaEjemplo = new RecetaDTO();
        recetaEjemplo.setId(1);
        recetaEjemplo.setPlatoId(1);
        recetaEjemplo.setIngredienteId(1);
        recetaEjemplo.setCantidadNecesaria(5.0);
    }

    /* ---  TESTS PARA INGREDIENTES --- */

    @Test
    void listarIngredientes_retorna200() throws Exception {
        // ARRANGE
        List<IngredienteDTO> lista = new ArrayList<>();
        lista.add(ingredienteEjemplo);
        when(service.listarTodosLosIngredientes()).thenReturn(lista);

        // ACT + ASSERT
        mockMvc.perform(get("/v1/orderflow/gestioninventario/ingredientes")
                .header("X-Rol-Empleado", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void buscarIngredientePorId_retorna404() throws Exception {
        // ARRANGE
        when(service.buscarIngredientePorId(99)).thenReturn(null);

        // ACT + ASSERT
        mockMvc.perform(get("/v1/orderflow/gestioninventario/ingredientes/99")
                .header("X-Rol-Empleado", "1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void crearIngrediente_retorna201() throws Exception {
        // ARRANGE
        when(service.crearIngrediente(any(IngredienteDTO.class))).thenReturn(ingredienteEjemplo);

        // ACT + ASSERT
        mockMvc.perform(post("/v1/orderflow/gestioninventario/ingredientes")
                .header("X-Rol-Empleado", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nombre\":\"Tomate\", \"stock\":50, \"unidad\":\"kg\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void crearIngrediente_retorna403SinRolAdmin() throws Exception {
        // ACT + ASSERT
        mockMvc.perform(post("/v1/orderflow/gestioninventario/ingredientes")
        .header("X-Rol-Empleado", "2") 
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"nombre\":\"Tomate\", \"stock\":50, \"unidad\":\"kg\"}"))
        .andExpect(status().isForbidden());
    }

    @Test
    void actualizarIngrediente_retorna200() throws Exception {
        // ARRANGE
        when(service.actualizarIngrediente(eq(1), any(IngredienteDTO.class))).thenReturn(ingredienteEjemplo);

        // ACT + ASSERT
        mockMvc.perform(put("/v1/orderflow/gestioninventario/ingredientes/1")
                .header("X-Rol-Empleado", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nombre\":\"Tomate\", \"stock\":40, \"unidad\":\"kg\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void eliminarIngrediente_retorna200() throws Exception {
        // ARRANGE
        doNothing().when(service).eliminarIngrediente(1);

        // ACT + ASSERT
        mockMvc.perform(delete("/v1/orderflow/gestioninventario/ingredientes/1")
                .header("X-Rol-Empleado", "1"))
                .andExpect(status().isOk());
    }

    /* ---  TESTS PARA RECETAS --- */

    @Test
    void listarRecetas_retorna200() throws Exception {
        // ARRANGE
        List<RecetaDTO> lista = new ArrayList<>();
        lista.add(recetaEjemplo);
        when(service.listarTodasLasRecetas()).thenReturn(lista);

        // ACT + ASSERT
        mockMvc.perform(get("/v1/orderflow/gestioninventario/recetas")
                .header("X-Rol-Empleado", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void listarPorPlato_retorna200() throws Exception {
        // ARRANGE
        List<RecetaDTO> lista = new ArrayList<>();
        when(service.listarRecetasPorPlato(10)).thenReturn(lista);

        // ACT + ASSERT
        mockMvc.perform(get("/v1/orderflow/gestioninventario/recetas/plato/10")
                .header("X-Rol-Empleado", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void crearReceta_retorna201() throws Exception {
        // ARRANGE
        when(service.crearReceta(any(RecetaDTO.class))).thenReturn(recetaEjemplo);

        // ACT + ASSERT
        mockMvc.perform(post("/v1/orderflow/gestioninventario/recetas")
                .header("X-Rol-Empleado", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"platoId\":1, \"ingredienteId\":2, \"cantidadNecesaria\":5.0}"))
                .andExpect(status().isCreated());
    }

    @Test
    void eliminarReceta_retorna200() throws Exception {
        // ARRANGE
        doNothing().when(service).eliminarReceta(1);

        // ACT + ASSERT
        mockMvc.perform(delete("/v1/orderflow/gestioninventario/recetas/1")
                .header("X-Rol-Empleado", "1"))
                .andExpect(status().isOk());
    }

    /* --- TEST PARA LÓGICA DE NEGOCIO INTERNA --- */

    @Test
    void descontarStock_retorna200Exito() throws Exception {
        // ARRANGE
        when(service.descontarStockPorPlato(1)).thenReturn(true);

        // ACT + ASSERT
        mockMvc.perform(post("/v1/orderflow/gestioninventario/descontar/1"))
                .andExpect(status().isOk());
    }

    @Test
    void descontarStock_retorna400Fallo() throws Exception {
        // ARRANGE: Simulamos que no hay stock suficiente o el plato no existe
        when(service.descontarStockPorPlato(99)).thenReturn(false);

        // ACT + ASSERT
        mockMvc.perform(post("/v1/orderflow/gestioninventario/descontar/99"))
                .andExpect(status().isBadRequest());
    }
}