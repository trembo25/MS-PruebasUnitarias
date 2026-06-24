package cl.orderflowmanagement.gestionproveedores.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import static org.hamcrest.Matchers.containsString;

import cl.orderflowmanagement.gestionproveedores.dto.EstadoFacturaDTO;
import cl.orderflowmanagement.gestionproveedores.dto.FacturaDTO;
import cl.orderflowmanagement.gestionproveedores.dto.ProveedorDTO;
import cl.orderflowmanagement.gestionproveedores.model.Factura;
import cl.orderflowmanagement.gestionproveedores.model.Proveedor;
import cl.orderflowmanagement.gestionproveedores.service.ProveedorService;

@WebMvcTest(ProveedorController.class) 
public class ProveedorControllerTest {

    @Autowired
    private MockMvc mockMvc; // Nos permite simular peticiones HTTP (GET, POST, etc.)

    @MockBean
    private ProveedorService proveedorService; // Registra un Mock dentro del contexto de Spring

    private ProveedorDTO proveEjemplo;
    private FacturaDTO facturaEjemplo;  // Declaramos la variable a nivel de clase
      private Factura facturaEntidad;

    @Autowired
private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        proveEjemplo = new ProveedorDTO();
        proveEjemplo.setId(1);
        proveEjemplo.setRut("26.242.268-9");
        proveEjemplo.setNombre("distribuidora-HH");
        proveEjemplo.setCorreo("contacto@distribuidorahh.cl");
        proveEjemplo.setFacturas(new ArrayList<>());

        facturaEjemplo = new FacturaDTO();
        facturaEjemplo.setId(1);
        facturaEjemplo.setNumeroFactura("FAC-2026-0089");
        facturaEjemplo.setMontoTotal(178500.0);
        facturaEjemplo.setEstadoPedido("pendiente");
        facturaEjemplo.setFechaEmision(new Date());

        facturaEntidad = new Factura();
        facturaEntidad.setNumeroFactura("FAC-2026-0089");
        facturaEntidad.setFechaEmision(new Date());
        facturaEntidad.setFechaVencimiento(new Date());
        facturaEntidad.setMontoNeto(150000.0);
        facturaEntidad.setIva(28500.0);
        facturaEntidad.setMontoTotal(178500.0);
        facturaEntidad.setEstadoPedido("pendiente");
    }

    @Test 
    void buscarPorId_retorna200() throws Exception {
        // ARRANGE
    
        when(proveedorService.buscarPorId(1)).thenReturn(proveEjemplo);

        // ACT & ASSERT
        mockMvc.perform(get("/v1/orderflow/gestionproveedores/1")
                .header("X-Rol-Empleado", "1")) 
                .andExpect(status().isOk()) 
                .andExpect(jsonPath("$.id").value(1)) 
                .andExpect(jsonPath("$.nombre").value("distribuidora-HH"));
    }

    @Test
    void buscarPorId_retorna403_CuandoRolNoEsValido() throws Exception {
        // ACT & ASSERT
        mockMvc.perform(get("/v1/orderflow/gestionproveedores/1")
                .header("X-Rol-Empleado", "2")) // Rol inválido
                .andExpect(status().isForbidden()); 
    }
    
      @Test
    void buscarPorId_retorna404_CuandoProveedorNoExiste() throws Exception {
        when(proveedorService.buscarPorId(99)).thenReturn(null);

        mockMvc.perform(get("/v1/orderflow/gestionproveedores/99")
                .header("X-Rol-Empleado", "1")) 
                .andExpect(status().isNotFound()); 
    }

    @Test
void buscarPorId_retorna500_CuandoServiceLanzaExcepcion() throws Exception {
    // ARRANGE
    when(proveedorService.buscarPorId(1))
            .thenThrow(new RuntimeException("Error en BD")); // Lanza excepción = 500

    // ACT & ASSERT
    mockMvc.perform(get("/v1/orderflow/gestionproveedores/1") // Mismo ID que el mock
            .header("X-Rol-Empleado", "1")) 
    .andExpect(status().isInternalServerError()); // 500
}


    @Test
    void listar_retorna200ConListaDeProveedores() throws Exception {
        // ARRANGE
        when(proveedorService.listarTodos()).thenReturn(List.of(proveEjemplo));

        // ACT & ASSERT
        mockMvc.perform(get("/v1/orderflow/gestionproveedores")
                .header("X-Rol-Empleado", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nombre").value("distribuidora-HH"));
    }

    @Test
    void listar_retorna403_CuandoRolNoEsValido() throws Exception {
        // ACT & ASSERT
        mockMvc.perform(get("/v1/orderflow/gestionproveedores")
                .header("X-Rol-Empleado", "2"))
                .andExpect(status().isForbidden());

        verify(proveedorService, never()).listarTodos();
    }

    @Test
    void listar_retorna500_CuandoServiceLanzaExcepcion() throws Exception {
        // ARRANGE
        when(proveedorService.listarTodos())
                .thenThrow(new RuntimeException("Error inesperado"));

        // ACT & ASSERT
        mockMvc.perform(get("/v1/orderflow/gestionproveedores")
                .header("X-Rol-Empleado", "1"))
                .andExpect(status().isInternalServerError());
    }

    @Test // nota arreglar el controller para qu develva un dto no un prove asi no tngo que 
    //crear una entidad nueva 
    void agregarProveedor_retorna200() throws Exception{
    // ARRANGE
        // Creamos la entidad que se enviará en el cuerpo del Request
        Proveedor proveedorEntidad = new Proveedor();
        proveedorEntidad.setRut("26.242.268-9");
        proveedorEntidad.setNombre("distribuidora-HH");
        proveedorEntidad.setCorreo("contacto@distribuidorahh.cl");

        // El servicio recibe la entidad Proveedor y devuelve el DTO (proveEjemplo)
        when(proveedorService.guardarProveedor(proveedorEntidad)).thenReturn(proveEjemplo);

        // ACT & ASSERT
        mockMvc.perform(post("/v1/orderflow/gestionproveedores")
                        .header("X-Rol-Empleado", "1") // Rol autorizado
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(proveedorEntidad))) // Enviamos la entidad mapeada a JSON
                .andExpect(status().isCreated()) // 201 Created según tu controlador
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("distribuidora-HH"));
    }

      @Test
    void agregarProveedor_retorna403_CuandoRolNoEsValido() throws Exception {
        Proveedor proveedorEntidad = new Proveedor();
        proveedorEntidad.setRut("26.242.268-9");
        proveedorEntidad.setNombre("distribuidora-HH");

        mockMvc.perform(post("/v1/orderflow/gestionproveedores")
                .header("X-Rol-Empleado", "2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(proveedorEntidad)))
                .andExpect(status().isForbidden());

        verify(proveedorService, never()).guardarProveedor(any());
    }

    @Test
    void agregarProveedor_retorna500_CuandoServiceLanzaExcepcion() throws Exception {
        Proveedor proveedorEntidad = new Proveedor();
        proveedorEntidad.setRut("26.242.268-9");
        proveedorEntidad.setNombre("distribuidora-HH");

        when(proveedorService.guardarProveedor(any(Proveedor.class)))
                .thenThrow(new RuntimeException("Error en BD"));

        mockMvc.perform(post("/v1/orderflow/gestionproveedores")
                .header("X-Rol-Empleado", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(proveedorEntidad)))
                .andExpect(status().isInternalServerError());
    }
    @Test
    void agregarFactura_retorna201_ConRolValido() throws Exception {
    when(proveedorService.guardarFactura(any(Factura.class))).thenReturn(facturaEjemplo);

    mockMvc.perform(post("/v1/orderflow/gestionproveedores/facturas")
            .header("X-Rol-Empleado", "1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(facturaEntidad)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.numeroFactura").value("FAC-2026-0089"))
            .andExpect(jsonPath("$.estadoPedido").value("pendiente"))
            .andExpect(jsonPath("$.montoTotal").value(178500.0));
    }
    @Test
    void agregarFactura_retorna403_CuandoRolNoEsValido() throws Exception {
    mockMvc.perform(post("/v1/orderflow/gestionproveedores/facturas")
            .header("X-Rol-Empleado", "2")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(facturaEntidad)))
            .andExpect(status().isForbidden());

    verify(proveedorService, never()).guardarFactura(any());
    }

    @Test
    void agregarFactura_retorna500_CuandoServiceLanzaExcepcion() throws Exception {
    when(proveedorService.guardarFactura(any(Factura.class)))
            .thenThrow(new RuntimeException("Error al guardar factura"));

    mockMvc.perform(post("/v1/orderflow/gestionproveedores/facturas")
            .header("X-Rol-Empleado", "1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(facturaEntidad)))
            .andExpect(status().isInternalServerError());
    }

    @Test
void actualizarEstadoFactura_retorna200_EstadoValido() throws Exception {
    EstadoFacturaDTO estadoDto = new EstadoFacturaDTO();
    estadoDto.setNuevoEstado("completado");
    
    FacturaDTO facturaActualizada = new FacturaDTO();
    facturaActualizada.setId(1);
    facturaActualizada.setNumeroFactura("FAC-2026-0089");
    facturaActualizada.setMontoTotal(178500.0);
    facturaActualizada.setEstadoPedido("completado"); // Estado actualizado
    facturaActualizada.setFechaEmision(new Date());

    when(proveedorService.actualizarEstadoFactura(1, "completado")).thenReturn(facturaActualizada);

    mockMvc.perform(put("/v1/orderflow/gestionproveedores/facturas/1/estado")
            .header("X-Rol-Empleado", "1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(estadoDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.estadoPedido").value("completado"));
}

@Test
void actualizarEstadoFactura_retorna403_CuandoRolNoEsValido() throws Exception {
    EstadoFacturaDTO estadoDto = new EstadoFacturaDTO();
    estadoDto.setNuevoEstado("completado");

    mockMvc.perform(put("/v1/orderflow/gestionproveedores/facturas/1/estado")
            .header("X-Rol-Empleado", "2")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(estadoDto)))
            .andExpect(status().isForbidden());

    verify(proveedorService, never()).actualizarEstadoFactura(anyInt(), any());
}

@Test
void actualizarEstadoFactura_retorna404_CuandoFacturaNoExiste() throws Exception {
    EstadoFacturaDTO estadoDto = new EstadoFacturaDTO();
    estadoDto.setNuevoEstado("completado");

    when(proveedorService.actualizarEstadoFactura(99, "completado")).thenReturn(null);

    mockMvc.perform(put("/v1/orderflow/gestionproveedores/facturas/99/estado")
            .header("X-Rol-Empleado", "1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(estadoDto)))
            .andExpect(status().isNotFound());
}

@Test
void actualizarEstadoFactura_retorna400_EstadoInvalido() throws Exception {
    EstadoFacturaDTO estadoDto = new EstadoFacturaDTO();
    estadoDto.setNuevoEstado("estadoInvalido");

    when(proveedorService.actualizarEstadoFactura(1, "estadoInvalido"))
            .thenThrow(new IllegalArgumentException("Estado no válido: estadoInvalido. Estados permitidos: pendiente, en camino, completado, cancelado"));

    mockMvc.perform(put("/v1/orderflow/gestionproveedores/facturas/1/estado")
            .header("X-Rol-Empleado", "1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(estadoDto)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$").value(containsString("Estado no válido")));
}

@Test
void actualizarEstadoFactura_retorna500_CuandoServiceLanzaExcepcion() throws Exception {
    EstadoFacturaDTO estadoDto = new EstadoFacturaDTO();
    estadoDto.setNuevoEstado("completado");

    when(proveedorService.actualizarEstadoFactura(1, "completado"))
            .thenThrow(new RuntimeException("Error en BD"));

    mockMvc.perform(put("/v1/orderflow/gestionproveedores/facturas/1/estado")
            .header("X-Rol-Empleado", "1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(estadoDto)))
            .andExpect(status().isInternalServerError());
}

 
}



