package cl.ordery.menu.menu_api.service;

import cl.ordery.menu.menu_api.dto.PlatoDTO;
import cl.ordery.menu.menu_api.model.Categoria;
import cl.ordery.menu.menu_api.model.Plato;
import cl.ordery.menu.menu_api.repository.CategoriaRepository;
import cl.ordery.menu.menu_api.repository.PlatoRepository;
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
public class PlatoServiceTest {

    @Mock
    private PlatoRepository platoRepo;

    @Mock
    private CategoriaRepository categoriaRepo;

    @InjectMocks
    private PlatoService platoService;

    private Categoria categoriaBase;
    private Plato platoBase;
    private PlatoDTO platoDTO;

    @BeforeEach
    void setUp() {
        categoriaBase = new Categoria(1, "Postres", "Dulces", new ArrayList<>());
        platoBase = new Plato(1, "Tiramisú", 4500.0, true, categoriaBase);
        
        platoDTO = new PlatoDTO();
        platoDTO.setNombre("Tiramisú");
        platoDTO.setPrecio(4500.0);
        platoDTO.setDisponible(true);
        platoDTO.setCategoriaId(1);
    }

    @Test
    void getPlatos_retornaListaConvertida() {
        // ARRANGE: preparamos los datos simulados
        List<Plato> listaBd = new ArrayList<>();
        listaBd.add(platoBase);
        when(platoRepo.findAll()).thenReturn(listaBd);

        // ACT: ejecutamos el listado
        List<PlatoDTO> resultado = platoService.getPlatos();

        // ASSERT: comprobamos la transformación de la lista
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Tiramisú", resultado.get(0).getNombre());
    }

    @Test
    void crearPlato_siCategoriaExiste_guardaYRetornaDTO() {
        // ARRANGE: aseguramos que la categoría asociada existe
        when(categoriaRepo.findById(1)).thenReturn(Optional.of(categoriaBase));
        when(platoRepo.save(any(Plato.class))).thenAnswer(i -> {
            Plato p = i.getArgument(0);
            p.setId(1);
            return p;
        });

        // ACT: creamos el plato en el servicio
        PlatoDTO resultado = platoService.crearPlato(platoDTO);

        // ASSERT: comprobamos la relación y el guardado
        assertNotNull(resultado);
        assertEquals("Tiramisú", resultado.getNombre());
        assertEquals(1, resultado.getCategoriaId());
        verify(platoRepo, times(1)).save(any(Plato.class));
    }

    @Test
    void crearPlato_siCategoriaNoExiste_retornaNull() {
        // ARRANGE: simulamos que la categoría solicitada no exista
        when(categoriaRepo.findById(99)).thenReturn(Optional.empty());
        platoDTO.setCategoriaId(99);

        // ACT: intentamos crear el plato sin su categoría main
        PlatoDTO resultado = platoService.crearPlato(platoDTO);

        // ASSERT: verificamos que el sistema rechace la operación
        assertNull(resultado);
        verify(platoRepo, never()).save(any(Plato.class)); // Valida que no datos erroneos a la bd
    }

    @Test
    void actualizarPlato_cambiaDisponibilidad() {
        // ARRANGE: simulamos encontrar el plato original
        when(platoRepo.findById(1)).thenReturn(Optional.of(platoBase));
        when(platoRepo.save(any(Plato.class))).thenReturn(platoBase);

        // ACT: actualizamos la disponibilidad a falso
        PlatoDTO resultado = platoService.actualizarPlato(1, false);

        // ASSERT: comprobamos el cambio de estado
        assertNotNull(resultado);
        assertFalse(resultado.getDisponible());
        verify(platoRepo, times(1)).save(any(Plato.class));
    }
}