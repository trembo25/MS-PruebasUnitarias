package cl.orderflowmanagement.gestionusuarios.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import cl.orderflowmanagement.gestionusuarios.model.Empleado;
import cl.orderflowmanagement.gestionusuarios.model.RolEmpleado;
import cl.orderflowmanagement.gestionusuarios.model.Usuario;
import cl.orderflowmanagement.gestionusuarios.service.EmpleadoService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmpleadoController.class)
public class EmpleadoControllerTest {

    @Autowired
    private MockMvc mockMvc; // Simula las peticiones HTTP

    @Autowired
    private ObjectMapper objectMapper; // Herramienta para convertir objetos Java a JSON (vital para POST y PUT)

    @MockBean
    private EmpleadoService empleadoService;

    private Empleado empleadoEjemplo;

    @BeforeEach
    void setUp() {
        // Inicializamos los objetos anidados requeridos por Empleado
        RolEmpleado rolEjemplo = new RolEmpleado(1, "admin");
        Usuario usuarioEjemplo = new Usuario(1, "pepito@restaurant.cl", "pepito1");

        // Construimos el empleado de prueba completo
        empleadoEjemplo = new Empleado();
        empleadoEjemplo.setId(1);
        empleadoEjemplo.setNombres("Juan");
        empleadoEjemplo.setApellidos("Pérez");
        empleadoEjemplo.setFechaNacimiento(new Date());
        empleadoEjemplo.setFechaContratacion(new Date());
        empleadoEjemplo.setSueldoBase(2000000.0);
        empleadoEjemplo.setDireccion("Av. Los Palotes 123");
        empleadoEjemplo.setRolEmpleado(rolEjemplo);
        empleadoEjemplo.setUsuario(usuarioEjemplo);
    }

    // --- TESTS PARA GET /{id} ---

    @Test
    void obtenerPorId_retorna200_cuandoEmpleadoExiste() throws Exception {
        // ARRANGE: Si el service busca el ID 1, retorna el empleado de prueba
        when(empleadoService.findEmpleadoById(1)).thenReturn(empleadoEjemplo);

        // ACT + ASSERT
        mockMvc.perform(get("/v1/orderflow/gestionempleados/1")
                .header("X-Rol-Empleado", "1")) // Simulamos que un admin hace la petición
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombres").value("Juan"))
                .andExpect(jsonPath("$.apellidos").value("Pérez"));
    }

    @Test
    void obtenerPorId_retorna404_cuandoEmpleadoNoExiste() throws Exception {
        // ARRANGE: Si busca el ID 99, retorna null
        when(empleadoService.findEmpleadoById(99)).thenReturn(null);

        // ACT + ASSERT
        mockMvc.perform(get("/v1/orderflow/gestionempleados/99")
                .header("X-Rol-Empleado", "1"))
                .andExpect(status().isNotFound());
    }

    // --- TESTS PARA GET (Listar todos) ---

    @Test
    void listar_retorna200_yListaDeEmpleados() throws Exception {
        // ARRANGE
        when(empleadoService.getAll()).thenReturn(Arrays.asList(empleadoEjemplo));

        // ACT + ASSERT
        mockMvc.perform(get("/v1/orderflow/gestionempleados")
                .header("X-Rol-Empleado", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1)) // Verifica que el JSON es un arreglo de tamaño 1
                .andExpect(jsonPath("$[0].nombres").value("Juan"));
    }

    @Test
    void listar_retorna403_cuandoRolNoEsAdmin() throws Exception {
        // ACT + ASSERT: Pasamos rol "2" (Garzón), el controlador debería bloquearlo
        mockMvc.perform(get("/v1/orderflow/gestionempleados")
                .header("X-Rol-Empleado", "2"))
                .andExpect(status().isForbidden());
    }

    // --- TESTS PARA POST ---

    @Test
    void guardar_retorna201_cuandoSeCreaExitosamente() throws Exception {
        // ARRANGE: Cuando se guarde cualquier empleado, retorna el de prueba
        when(empleadoService.addEmpleado(any(Empleado.class))).thenReturn(empleadoEjemplo);

        // ACT + ASSERT
        mockMvc.perform(post("/v1/orderflow/gestionempleados")
                .header("X-Rol-Empleado", "1")
                .contentType(MediaType.APPLICATION_JSON) // Le decimos que enviamos JSON
                .content(objectMapper.writeValueAsString(empleadoEjemplo))) // Convertimos el objeto a texto JSON
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    // --- TESTS PARA DELETE ---

    @Test
    void eliminar_retorna204_cuandoBorradoEsExitoso() throws Exception {
        // ARRANGE: Para eliminar, primero el controlador busca que exista
        when(empleadoService.findEmpleadoById(1)).thenReturn(empleadoEjemplo);
        doNothing().when(empleadoService).deleteEmpleado(1); // Simulamos un void que no hace nada

        // ACT + ASSERT
        mockMvc.perform(delete("/v1/orderflow/gestionempleados/1")
                .header("X-Rol-Empleado", "1"))
                .andExpect(status().isNoContent()); // 204 No Content
    }

    // --- TESTS FALTANTES PARA POST (Sad Path) ---

    @Test
    void guardar_retorna400_cuandoFallaCreacion() throws Exception {
        // ARRANGE: Simulamos que el service falla y retorna null
        when(empleadoService.addEmpleado(any(Empleado.class))).thenReturn(null);

        // ACT + ASSERT
        mockMvc.perform(post("/v1/orderflow/gestionempleados")
                .header("X-Rol-Empleado", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(empleadoEjemplo)))
                .andExpect(status().isBadRequest()); // Esperamos un 400
    }

    // --- TESTS FALTANTES PARA DELETE (Sad Path) ---

    @Test
    void eliminar_retorna404_cuandoEmpleadoNoExiste() throws Exception {
        // ARRANGE: Simulamos que al buscar el ID antes de borrar, no existe
        when(empleadoService.findEmpleadoById(99)).thenReturn(null);

        // ACT + ASSERT
        mockMvc.perform(delete("/v1/orderflow/gestionempleados/99")
                .header("X-Rol-Empleado", "1"))
                .andExpect(status().isNotFound()); // Esperamos un 404
    }

    // --- TESTS PARA PUT (Actualizar) ---

    @Test
    void actualizar_retorna200_cuandoSeActualizaExitosamente() throws Exception {
        // ARRANGE: Simulamos que el servicio logra actualizar y retorna el objeto
        when(empleadoService.updateEmpleado(any(Empleado.class))).thenReturn(empleadoEjemplo);

        // ACT + ASSERT
        mockMvc.perform(put("/v1/orderflow/gestionempleados")
                .header("X-Rol-Empleado", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(empleadoEjemplo)))
                .andExpect(status().isOk()) // Esperamos un 200 OK
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void actualizar_retorna404_cuandoEmpleadoNoExiste() throws Exception {
        // ARRANGE: Simulamos que el servicio no encontró al empleado a actualizar (retorna null)
        when(empleadoService.updateEmpleado(any(Empleado.class))).thenReturn(null);

        // ACT + ASSERT
        mockMvc.perform(put("/v1/orderflow/gestionempleados")
                .header("X-Rol-Empleado", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(empleadoEjemplo)))
                .andExpect(status().isNotFound()); // Esperamos un 404 Not Found
    }
}