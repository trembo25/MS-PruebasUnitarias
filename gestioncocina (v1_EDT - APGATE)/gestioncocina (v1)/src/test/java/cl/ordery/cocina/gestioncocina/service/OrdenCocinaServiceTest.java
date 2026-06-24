package cl.ordery.cocina.gestioncocina.service;

import cl.ordery.cocina.gestioncocina.model.OrdenCocina;
import cl.ordery.cocina.gestioncocina.repository.OrdenCocinaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Usamos Mockito para probar lógica de negocio
public class OrdenCocinaServiceTest {

    @Mock
    private OrdenCocinaRepository cocinaRepo; // Simulamos la Base de Datos

    @InjectMocks
    private OrdenCocinaService cocinaService; // El servicio real que estamos probando

    private OrdenCocina ordenEjemplo;

    @BeforeEach
    void setUp() {
        ordenEjemplo = new OrdenCocina();
        ordenEjemplo.setId(1);
        ordenEjemplo.setPedidoId(10);
        ordenEjemplo.setProductoId(5);
        ordenEjemplo.setCantidad(2);
        ordenEjemplo.setEstado("Pendiente");
    }

    @Test
    void listarOrdenes_retornaListaDeOrdenes() {
        // ARRANGE: Preparamos la respuesta de la BD falsa
        List<OrdenCocina> lista = new ArrayList<>();
        lista.add(ordenEjemplo);
        when(cocinaRepo.findAll()).thenReturn(lista);

        // ACT: Ejecutamos el método del servicio
        List<OrdenCocina> resultado = cocinaService.listarOrdenes();

        // ASSERT: Verificamos que se hya realizado el trabajo
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(10, resultado.get(0).getPedidoId());
        verify(cocinaRepo, times(1)).findAll();
    }

    @Test
    void guardarOrden_asignaEstadoPendienteYFecha() {
        // ARRANGE
        OrdenCocina nuevaOrden = new OrdenCocina();
        nuevaOrden.setPedidoId(10);
        nuevaOrden.setProductoId(5);
        nuevaOrden.setCantidad(2);

        // Simulamos que al guardar se le asigna el ID 1
        when(cocinaRepo.save(any(OrdenCocina.class))).thenAnswer(invocation -> {
            OrdenCocina ordenGuardada = invocation.getArgument(0);
            ordenGuardada.setId(1); 
            return ordenGuardada;
        });

        // ACT
        OrdenCocina resultado = cocinaService.guardarOrden(nuevaOrden);

        // ASSERT: Validamos la regla de negocio
        assertNotNull(resultado);
        assertEquals("Pendiente", resultado.getEstado()); // Verificamos que sí asigne "Pendiente"
        assertNotNull(resultado.getFechaRegistro()); // Verificamos que sí asigne la fecha
        assertEquals(1, resultado.getId());
        verify(cocinaRepo, times(1)).save(any(OrdenCocina.class));
    }

    @Test
    void actualizarEstado_siExisteOrden_retornaOrdenActualizada() {
        // ARRANGE
        when(cocinaRepo.findById(1)).thenReturn(Optional.of(ordenEjemplo));
        when(cocinaRepo.save(any(OrdenCocina.class))).thenReturn(ordenEjemplo);

        // ACT
        OrdenCocina resultado = cocinaService.actualizarEstado(1, "En preparacion");

        // ASSERT
        assertNotNull(resultado);
        assertEquals("En preparacion", resultado.getEstado());
        verify(cocinaRepo, times(1)).findById(1);
        verify(cocinaRepo, times(1)).save(ordenEjemplo);
    }

    @Test
    void actualizarEstado_siNoExisteOrden_retornaNull() {
        // ARRANGE
        when(cocinaRepo.findById(99)).thenReturn(Optional.empty());

        // ACT
        OrdenCocina resultado = cocinaService.actualizarEstado(99, "En preparacion");

        // ASSERT
        assertNull(resultado);
        verify(cocinaRepo, times(1)).findById(99);
        verify(cocinaRepo, never()).save(any(OrdenCocina.class)); // Verificamos que NO haya guardado nada
    }
}