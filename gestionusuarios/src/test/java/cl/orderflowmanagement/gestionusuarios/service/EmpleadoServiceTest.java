package cl.orderflowmanagement.gestionusuarios.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cl.orderflowmanagement.gestionusuarios.model.Empleado;
import cl.orderflowmanagement.gestionusuarios.model.RolEmpleado;
import cl.orderflowmanagement.gestionusuarios.model.Usuario;
import cl.orderflowmanagement.gestionusuarios.repository.EmpleadoRepository;
import cl.orderflowmanagement.gestionusuarios.repository.RolEmpleadoRepository;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

// A diferencia del controller, aquí no levantamos el contexto web, solo usamos Mockito
@ExtendWith(MockitoExtension.class)
public class EmpleadoServiceTest {

    // Simulamos los repositorios para no tocar la base de datos real
    @Mock
    private EmpleadoRepository empleadoRepository;

    @Mock
    private RolEmpleadoRepository rolEmpleadoRepository;

    // Inyectamos los mocks anteriores directamente en tu servicio real
    @InjectMocks
    private EmpleadoService empleadoService;

    private Empleado empleadoEjemplo;

    @BeforeEach
    void setUp() {
        RolEmpleado rolEjemplo = new RolEmpleado(1, "admin");
        Usuario usuarioEjemplo = new Usuario(1, "pepito@restaurant.cl", "pepito1");

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

    @Test
    void getAll_retornaListaDeEmpleados() {
        // ARRANGE: Cuando el repositorio busque a todos, devolvemos una lista falsa con nuestro ejemplo
        when(empleadoRepository.findAll()).thenReturn(Arrays.asList(empleadoEjemplo));

        // ACT: Llamamos al método real de tu servicio
        List<Empleado> resultado = empleadoService.getAll();

        // ASSERT: Verificamos que no sea nulo y que el tamaño sea 1
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Juan", resultado.get(0).getNombres());
    }

    @Test
    void findEmpleadoById_retornaEmpleado_cuandoExiste() {
        // ARRANGE: Simulamos que la base de datos encuentra al empleado y lo envuelve en un Optional
        when(empleadoRepository.findById(1)).thenReturn(Optional.of(empleadoEjemplo));

        // ACT
        Empleado resultado = empleadoService.findEmpleadoById(1);

        // ASSERT
        assertNotNull(resultado);
        assertEquals("Pérez", resultado.getApellidos());
    }

    @Test
    void findEmpleadoById_retornaNull_cuandoNoExiste() {
        // ARRANGE: Simulamos que la base de datos no lo encuentra (Optional vacío)
        when(empleadoRepository.findById(99)).thenReturn(Optional.empty());

        // ACT
        Empleado resultado = empleadoService.findEmpleadoById(99);

        // ASSERT
        assertNull(resultado);
    }

    @Test
    void addEmpleado_guardaYRetornaEmpleado() {
        // ARRANGE: Al hacer save(), devolvemos el mismo objeto
        when(empleadoRepository.save(any(Empleado.class))).thenReturn(empleadoEjemplo);

        // ACT
        Empleado resultado = empleadoService.addEmpleado(empleadoEjemplo);

        // ASSERT
        assertNotNull(resultado);
        assertEquals("Juan", resultado.getNombres());
        // Verificamos que el método save() del repositorio fue llamado exactamente 1 vez
        verify(empleadoRepository, times(1)).save(empleadoEjemplo);
    }

    @Test
    void deleteEmpleado_ejecutaSinErrores() {
        // ARRANGE: Simular que el borrado no hace nada (void)
        doNothing().when(empleadoRepository).deleteById(1);

        // ACT
        empleadoService.deleteEmpleado(1);

        // ASSERT: Como es un void y no retorna nada, nuestra mejor prueba es verificar
        // que el repositorio realmente intentó borrar el ID 1 una vez.
        verify(empleadoRepository, times(1)).deleteById(1);
    }

    @Test
    void updateEmpleado_actualizaYRetornaEmpleado_cuandoExiste() {
        // ARRANGE: Creamos datos nuevos que vendrían en la petición
        Empleado datosNuevos = new Empleado();
        datosNuevos.setId(1);
        datosNuevos.setNombres("Juan Carlos"); // Nombre modificado
        datosNuevos.setApellidos("Pérez");
        datosNuevos.setUsuario(new Usuario(1, "nuevo@restaurant.cl", "123"));

        // Simulamos que el repositorio encuentra al empleado antiguo en la BD
        when(empleadoRepository.findById(1)).thenReturn(Optional.of(empleadoEjemplo));
        
        // Simulamos que al guardar, retorna el antiguo ya modificado
        when(empleadoRepository.save(any(Empleado.class))).thenReturn(empleadoEjemplo);

        // ACT
        Empleado resultado = empleadoService.updateEmpleado(datosNuevos);

        // ASSERT
        assertNotNull(resultado);
        // Comprobamos que el servicio traspasó los datos nuevos al empleado antiguo
        assertEquals("Juan Carlos", resultado.getNombres());
        assertEquals("nuevo@restaurant.cl", resultado.getUsuario().getCorreo());
    }

    @Test
    void updateEmpleado_retornaNull_cuandoNoExiste() {
        // ARRANGE: Simulamos que la base de datos no encuentra el ID que queremos actualizar
        when(empleadoRepository.findById(anyInt())).thenReturn(Optional.empty());

        // ACT: Intentamos actualizar usando nuestro empleado de ejemplo
        Empleado resultado = empleadoService.updateEmpleado(empleadoEjemplo);

        // ASSERT: El resultado debe ser nulo porque el if (empleadoAntiguo != null) no se cumplió
        assertNull(resultado);
        
        // BONUS MOCKITO: Verificamos que NUNCA se haya llamado al método save() por accidente
        verify(empleadoRepository, never()).save(any(Empleado.class));
    }
}