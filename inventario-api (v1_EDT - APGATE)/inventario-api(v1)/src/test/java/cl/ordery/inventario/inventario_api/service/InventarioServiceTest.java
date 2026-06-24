package cl.ordery.inventario.inventario_api.service;

import cl.ordery.inventario.inventario_api.client.MenuClient;
import cl.ordery.inventario.inventario_api.dto.IngredienteDTO;
import cl.ordery.inventario.inventario_api.dto.RecetaDTO;
import cl.ordery.inventario.inventario_api.model.Ingrediente;
import cl.ordery.inventario.inventario_api.model.Receta;
import cl.ordery.inventario.inventario_api.repository.IngredienteRepository;
import cl.ordery.inventario.inventario_api.repository.RecetaRepository;
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

@ExtendWith(MockitoExtension.class)
public class InventarioServiceTest {

    @Mock
    private IngredienteRepository ingredienteRepo;

    @Mock
    private RecetaRepository recetaRepo;

    @Mock
    private MenuClient menuClient; // Simulamos la conexión con el ms de Menú

    @InjectMocks
    private InventarioService inventarioService;

    private Ingrediente ingredienteBase;
    private Receta recetaBase;

    @BeforeEach
    void setUp() {
        ingredienteBase = new Ingrediente(1, "Harina", 100.0, "kg");
        recetaBase = new Receta(1, 10, 2.5, ingredienteBase); // PlatoId=10 requiere 2.5kg de harina
    }

    // --- TESTS PARA INGREDIENTE ---

    @Test
    void listarTodosLosIngredientes_retornaListaConvertidaADTO() {
        // ARRANGE
        List<Ingrediente> lista = new ArrayList<>();
        lista.add(ingredienteBase);
        when(ingredienteRepo.findAll()).thenReturn(lista);

        // ACT
        List<IngredienteDTO> resultado = inventarioService.listarTodosLosIngredientes();

        // ASSERT
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Harina", resultado.get(0).getNombre());
    }

    @Test
    void buscarIngredientePorId_retornaIngredienteDtoSiExiste() {
        // ARRANGE
        when(ingredienteRepo.findById(1)).thenReturn(Optional.of(ingredienteBase));

        // ACT
        IngredienteDTO resultado = inventarioService.buscarIngredientePorId(1);

        // ASSERT
        assertNotNull(resultado);
        assertEquals(100.0, resultado.getStock());
    }

    /* --- TESTS PARA RECETA --- */

    @Test
    void crearReceta_crearSiPlatoYIngredienteExisten() {
        // ARRANGE
        RecetaDTO dtoEntrada = new RecetaDTO(null, 10, 1, 2.5);

        // Simulamos que el feign client responde con un objeto genérico (el plato existe)
        when(menuClient.buscarPlatoPorId(10)).thenReturn(new Object());
        when(ingredienteRepo.findById(1)).thenReturn(Optional.of(ingredienteBase));

        // Simulamos el guardado
        when(recetaRepo.save(any(Receta.class))).thenAnswer(i -> {
            Receta rec = i.getArgument(0);
            rec.setId(1);
            return rec;
        });

        // ACT
        RecetaDTO resultado = inventarioService.crearReceta(dtoEntrada);

        // ASSERT
        assertNotNull(resultado);
        assertEquals(10, resultado.getPlatoId());
        verify(recetaRepo, times(1)).save(any(Receta.class));
    }

    @Test
    void crearReceta_retornaNullSiPlatoNoExisteEnMenu() {
        // ARRANGE
        RecetaDTO dtoEntrada = new RecetaDTO(null, 99, 1, 2.5);

        // Simulamos que el feign client no encuentra el plato
        when(menuClient.buscarPlatoPorId(99)).thenReturn(null);
        when(ingredienteRepo.findById(1)).thenReturn(Optional.of(ingredienteBase));

        // ACT
        RecetaDTO resultado = inventarioService.crearReceta(dtoEntrada);

        // ASSERT
        assertNull(resultado);
        verify(recetaRepo, never()).save(any(Receta.class)); // Verificamos que evite guardar basura
    }

    /*  --- TEST LÓGICA DE NEGOCIO (DESCONTAR STOCK) --- */ 

    @Test
    void descontarStockPorPlato_descuentaCorrectamente() {
        // ARRANGE
        List<Receta> recetasPlato = new ArrayList<>();
        recetasPlato.add(recetaBase); // Stock inicial: 100.0, Requiere: 2.5
        when(recetaRepo.findByPlatoId(10)).thenReturn(recetasPlato);

        // ACT
        boolean resultado = inventarioService.descontarStockPorPlato(10);

        // ASSERT
        assertTrue(resultado);
        assertEquals(97.5, ingredienteBase.getStock()); // Verificamos el cálculo matemático
        verify(ingredienteRepo, times(1)).save(ingredienteBase);
    }

    @Test
    void descontarStockPorPlato_fallaPorStockInsuficiente() {
        // ARRANGE: Configuramos un ingrediente con poco stock
        Ingrediente ingredientePocoStock = new Ingrediente(2, "Sal", 1.0, "kg");
        Receta recetaExigente = new Receta(2, 11, 5.0, ingredientePocoStock); // Requiere 5.0, solo hay 1.0
        
        List<Receta> recetasPlato = new ArrayList<>();
        recetasPlato.add(recetaExigente);
        when(recetaRepo.findByPlatoId(11)).thenReturn(recetasPlato);

        // ACT
        boolean resultado = inventarioService.descontarStockPorPlato(11);

        // ASSERT
        assertFalse(resultado);
        verify(ingredienteRepo, never()).save(any(Ingrediente.class)); // Se asegura de que no guarde un inventario en datos negativos
    }
}