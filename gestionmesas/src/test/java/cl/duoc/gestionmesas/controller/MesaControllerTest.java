package cl.duoc.gestionmesas.controller;

import cl.duoc.gestionmesas.dto.EstadoMesaDTO;
import cl.duoc.gestionmesas.model.Mesa;
import cl.duoc.gestionmesas.service.MesaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MesaController.class)
public class MesaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MesaService mesaService;

    private Mesa mesaEjemplo;
    private EstadoMesaDTO estadoMesaDTO;

    @BeforeEach
    void setUp() {
        mesaEjemplo = new Mesa(1, 10, 4, "disponible");
        
        estadoMesaDTO = new EstadoMesaDTO();
        estadoMesaDTO.setEstado("ocupada");
    }

    // ==========================================
    // GET: Listar Mesas
    // ==========================================
    @Test
    void listar_retorna200_conListaDeMesas() throws Exception {
        when(mesaService.listarMesas()).thenReturn(Arrays.asList(mesaEjemplo));

        mockMvc.perform(get("/v1/orderflow/gestionmesas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].numero").value(10));
    }

    @Test
    void listar_retorna500_cuandoOcurreUnErrorInterno() throws Exception {
        when(mesaService.listarMesas()).thenThrow(new RuntimeException("Error de BD"));

        mockMvc.perform(get("/v1/orderflow/gestionmesas"))
                .andExpect(status().isInternalServerError());
    }

    // ==========================================
    // POST: Guardar Mesa
    // ==========================================
    @Test
    void guardar_retorna201_cuandoSeCreaMesa() throws Exception {
        when(mesaService.guardarMesa(any(Mesa.class))).thenReturn(mesaEjemplo);

        mockMvc.perform(post("/v1/orderflow/gestionmesas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mesaEjemplo)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void guardar_retorna500_cuandoFallaElGuardado() throws Exception {
        when(mesaService.guardarMesa(any(Mesa.class))).thenThrow(new RuntimeException("Error general"));

        mockMvc.perform(post("/v1/orderflow/gestionmesas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mesaEjemplo)))
                .andExpect(status().isInternalServerError());
    }

    // ==========================================
    // DELETE: Eliminar Mesa
    // ==========================================
    @Test
    void eliminar_retorna204_cuandoBorradoEsExitoso() throws Exception {
        doNothing().when(mesaService).eliminarMesa(1);

        mockMvc.perform(delete("/v1/orderflow/gestionmesas/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void eliminar_retorna404_cuandoLaMesaNoExiste() throws Exception {
        doThrow(new RuntimeException("Mesa no encontrada")).when(mesaService).eliminarMesa(99);

        mockMvc.perform(delete("/v1/orderflow/gestionmesas/99"))
                .andExpect(status().isNotFound());
    }

    // ==========================================
    // PUT: Actualizar Estado
    // ==========================================
    @Test
    void actualizarEstado_retorna200_cuandoEstadoEsActualizado() throws Exception {
        mesaEjemplo.setEstado("ocupada");
        when(mesaService.actualizarMesa(eq(1), any(String.class))).thenReturn(mesaEjemplo);

        mockMvc.perform(put("/v1/orderflow/gestionmesas/1/estado")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(estadoMesaDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ocupada"));
    }

    @Test
    void actualizarEstado_retorna400_cuandoEstadoEsInvalido() throws Exception {
        when(mesaService.actualizarMesa(eq(1), any(String.class))).thenThrow(new IllegalArgumentException("Estado invalido"));

        mockMvc.perform(put("/v1/orderflow/gestionmesas/1/estado")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(estadoMesaDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void actualizarEstado_retorna404_cuandoMesaNoExiste() throws Exception {
        when(mesaService.actualizarMesa(eq(99), any(String.class))).thenThrow(new RuntimeException("Mesa no encontrada"));

        mockMvc.perform(put("/v1/orderflow/gestionmesas/99/estado")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(estadoMesaDTO)))
                .andExpect(status().isNotFound());
    }

    // ==========================================
    // GET: Obtener por ID
    // ==========================================
    @Test
    void obtenerPorId_retorna200_cuandoExiste() throws Exception {
        when(mesaService.buscarMesaPorId(1)).thenReturn(mesaEjemplo);

        mockMvc.perform(get("/v1/orderflow/gestionmesas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void obtenerPorId_retorna404_cuandoNoExiste() throws Exception {
        when(mesaService.buscarMesaPorId(99)).thenThrow(new RuntimeException("Mesa no existe"));

        mockMvc.perform(get("/v1/orderflow/gestionmesas/99"))
                .andExpect(status().isNotFound());
    }
}