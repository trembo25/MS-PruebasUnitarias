package cl.ordery.menu.menu_api.service;

import cl.ordery.menu.menu_api.dto.CategoriaDTO;
import cl.ordery.menu.menu_api.model.Categoria;
import cl.ordery.menu.menu_api.repository.CategoriaRepository;
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
public class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepo;

    @InjectMocks
    private CategoriaService categoriaService;

    private Categoria categoriaBase;
    private CategoriaDTO categoriaDTO;

    @BeforeEach
    void setUp() {
        categoriaBase = new Categoria(1, "Postres", "Cosas dulces", new ArrayList<>());
        categoriaDTO = new CategoriaDTO();
        categoriaDTO.setNombre("Postres");
        categoriaDTO.setDescripcion("Cosas dulces");
    }

    @Test
    void getCategorias_retornaListaConvertida() {
        // ARRANGE: preparamos la lista simulada de la BD
        List<Categoria> listaBd = new ArrayList<>();
        listaBd.add(categoriaBase);
        when(categoriaRepo.findAll()).thenReturn(listaBd);

        // ACT: ejecutamos el método del servicio
        List<CategoriaDTO> resultado = categoriaService.getCategorias();

        // ASSERT: verificamos que la lista se transformó correctamente
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Postres", resultado.get(0).getNombre());
    }

    @Test
    void getCategoriaById_retornaDTOsiExiste() {
        // ARRANGE: el repositorio encuentra la categoría
        when(categoriaRepo.findById(1)).thenReturn(Optional.of(categoriaBase));

        // ACT: realizamos la búsqueda por ID
        CategoriaDTO resultado = categoriaService.getCategoriaById(1);

        // ASSERT: verificamos que el dato coincida
        assertNotNull(resultado);
        assertEquals("Postres", resultado.getNombre());
    }

    @Test
    void createCategoria_guardaYRetornaDTO() {
        // ARRANGE: configuramos el guardado simulado
        when(categoriaRepo.save(any(Categoria.class))).thenAnswer(i -> {
            Categoria cat = i.getArgument(0);
            cat.setId(1);
            return cat;
        });

        // ACT: creamos la categoría a través del service
        CategoriaDTO resultado = categoriaService.createCategoria(categoriaDTO);

        // ASSERT: verificamos que el ID se asignó y se guardó en la lista simulada de la BD
        assertNotNull(resultado);
        assertEquals("Postres", resultado.getNombre());
        verify(categoriaRepo, times(1)).save(any(Categoria.class));
    }

    @Test
    void actualizarCategoria_retornaNullSiNoExiste() {
        // ARRANGE: el repositorio NO encuentra la categoría
        when(categoriaRepo.findById(99)).thenReturn(Optional.empty());

        // ACT: intentamos actualizar un ID inexistente
        CategoriaDTO resultado = categoriaService.actualizarCategoria(99, categoriaDTO);

        // ASSERT: verificamos que no haga nada y no guarde un dato erroneo
        assertNull(resultado);
        verify(categoriaRepo, never()).save(any(Categoria.class));
    }
}