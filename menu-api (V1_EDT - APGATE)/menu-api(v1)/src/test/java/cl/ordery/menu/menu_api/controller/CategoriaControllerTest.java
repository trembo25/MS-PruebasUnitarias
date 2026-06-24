package cl.ordery.menu.menu_api.controller;

import cl.ordery.menu.menu_api.dto.CategoriaDTO;
import cl.ordery.menu.menu_api.service.CategoriaService;
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

@WebMvcTest(CategoriaController.class) 
public class CategoriaControllerTest {

    @Autowired
    private MockMvc mockMvc; // simula las peticiones HTTP

    @MockBean
    private CategoriaService service; // servicio falso

    private CategoriaDTO categoriaEjemplo;

    @BeforeEach
    void setUp() {
        categoriaEjemplo = new CategoriaDTO();
        categoriaEjemplo.setId(1);
        categoriaEjemplo.setNombre("Bebidas");
    }

    @Test
    void listarTodas_retorna200ConCategorias() throws Exception {
        // ARRANGE: creamos la lista falsa
        List<CategoriaDTO> listaFalsa = new ArrayList<>();
        listaFalsa.add(categoriaEjemplo);

        // le decimos al servicio falso que devuelva esa lista
        when(service.getCategorias()).thenReturn(listaFalsa);

        // ACT + ASSERT: simulamos el GET y verificamos la respuesta
        mockMvc.perform(get("/v1/orderflow/gestionmenu/categorias")
                .header("X-Rol-Empleado", "1"))
                .andExpect(status().isOk())                            // código HTTP 200
               .andExpect(jsonPath("$[0].nombre").value("Bebidas"));  // primer elemento de la lista
    }

    @Test
    void listarTodas_retorna403SinPermisos() throws Exception {
        // ACT + ASSERT: simulamos petición con rol denegado
        mockMvc.perform(get("/v1/orderflow/gestionmenu/categorias")
                .header("X-Rol-Empleado", "99"))
                .andExpect(status().isForbidden());                    // código HTTP 403
    }

    @Test
    void obtenerPorId_retorna200() throws Exception {
        // ARRANGE: el servicio devuelve la categoria
        when(service.getCategoriaById(1)).thenReturn(categoriaEjemplo);

        // ACT + ASSERT
        mockMvc.perform(get("/v1/orderflow/gestionmenu/categorias/1")
                .header("X-Rol-Empleado", "2"))
                .andExpect(status().isOk())                            // código HTTP 200
                .andExpect(jsonPath("nombre").value("Bebidas"));
    }

    @Test
    void obtenerPorId_retorna404() throws Exception {
        // ARRANGE: el servicio devuelve nulo
        when(service.getCategoriaById(99)).thenReturn(null);

        // ACT + ASSERT
        mockMvc.perform(get("/v1/orderflow/gestionmenu/categorias/99")
                .header("X-Rol-Empleado", "1"))
                .andExpect(status().isNotFound());                     // código HTTP 404
    }

    @Test
    void guardar_retorna201() throws Exception {
        // ARRANGE
        when(service.createCategoria(any(CategoriaDTO.class))).thenReturn(categoriaEjemplo);

        // ACT + ASSERT
        mockMvc.perform(post("/v1/orderflow/gestionmenu/categorias")
                .header("X-Rol-Empleado", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nombre\":\"Bebidas\", \"descripcion\":\"Bebidas frías\"}"))
                .andExpect(status().isCreated())                       // código HTTP 201
                .andExpect(jsonPath("nombre").value("Bebidas"));
    }

    @Test
    void actualizar_retorna200() throws Exception {
        // ARRANGE
        when(service.actualizarCategoria(eq(1), any(CategoriaDTO.class))).thenReturn(categoriaEjemplo);

        // ACT + ASSERT
        mockMvc.perform(put("/v1/orderflow/gestionmenu/categorias/1")
                .header("X-Rol-Empleado", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nombre\":\"Bebidas Actualizadas\", \"descripcion\":\"Bebidas frías y calientes\"}"))
                .andExpect(status().isOk());                           // código HTTP 200
    }

    @Test
    void eliminar_retorna204() throws Exception {
        // ARRANGE: el servicio comprueba existencia y elimina
        when(service.getCategoriaById(1)).thenReturn(categoriaEjemplo);
        doNothing().when(service).deleteCategoria(1);

        // ACT + ASSERT
        mockMvc.perform(delete("/v1/orderflow/gestionmenu/categorias/1")
                .header("X-Rol-Empleado", "1"))
                .andExpect(status().isNoContent());                    // código HTTP 204
    }
}