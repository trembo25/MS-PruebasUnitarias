package cl.orderflowmanagement.gestionproveedores.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cl.orderflowmanagement.gestionproveedores.dto.FacturaDTO;
import cl.orderflowmanagement.gestionproveedores.dto.ProveedorDTO;
import cl.orderflowmanagement.gestionproveedores.model.Factura;
import cl.orderflowmanagement.gestionproveedores.model.Proveedor;
import cl.orderflowmanagement.gestionproveedores.repository.FacturaRepository;
import cl.orderflowmanagement.gestionproveedores.repository.ProveedorRepository;

@ExtendWith(MockitoExtension.class)
public class ProveedorServiceTest {
    /* creamos @Mock de proveedorRepository , para simular 
    los metodos del repository (es de mentira) */

    @Mock
    private ProveedorRepository proveedorRepository;

    @Mock
    private FacturaRepository facturaRepository;

    
    /* como vamos a testear los metodos de proveedorService
    creamos un objeto de esa clase ,pero con @injectMock
    insertamos el Mock de repository para que no use el real, si no el mock  */

    @InjectMocks
    private ProveedorService proveedorService;
    

    private Proveedor proveEjemplo;
    private Factura facturaEjemplo;
    
    @BeforeEach // antes de ejecutar las pruebas setea al prove 
    void setUp(){

         facturaEjemplo = new Factura();
        facturaEjemplo.setId(1);
        facturaEjemplo.setNumeroFactura("F-001");
        facturaEjemplo.setMontoTotal(120000.0);
        facturaEjemplo.setEstadoPedido("PENDIENTE");
        facturaEjemplo.setFechaEmision(new Date());

        
        proveEjemplo = new Proveedor();
        proveEjemplo.setId(1);
        proveEjemplo.setNombre("distribuidora-HH");
        proveEjemplo.setRut("26.242.268-9");
        proveEjemplo.setRazonSocial("12546-22");
        proveEjemplo.setFacturas(new ArrayList<>());

        }

 
        


    
    @Test
    void listar(){

        // ARRANGE : CREAMOS UNA LISTA VACIA YA AGREGAMOS EL PROVEEDOR DE EJEMPLO

        List<Proveedor> listaPrueba = new ArrayList<>();
        listaPrueba.add(proveEjemplo);

        // le decimo al mock que nos retorne la lista, cuando llamemos al metodo findAll

        when(proveedorRepository.findAll()).thenReturn(listaPrueba);

        // ACT
        //Llamamos al metodo real del service 
        List<ProveedorDTO> listarTodos = proveedorService.listarTodos();

        // ASSERT 
        // VERIFICAMOS EL RESULTADO
        assertEquals(1, listarTodos.size());
        assertEquals("distribuidora-HH", listarTodos.get(0).getNombre());
    }


   @Test
    void buscarPorId_encontrado() {
        // ARRANGE: El repositorio devuelve un Optional de la ENTIDAD (Proveedor)
        when(proveedorRepository.findById(1)).thenReturn(Optional.of(proveEjemplo));

        // ACT: El servicio devuelve un DTO (ProveedorDTO)
        ProveedorDTO resultado = proveedorService.buscarPorId(1);

        // ASSERT
        assertNotNull(resultado);
        assertEquals(1, resultado.getId());
        assertEquals("distribuidora-HH", resultado.getNombre());

    }

   @Test
void buscarPorId_noEncontrado() {
    // ARRANGE
    
    
    // Le decimos al mock: "Cuando te busquen por el ID 99, simula que no encontraste nada en la BD"
    when(proveedorRepository.findById(99)).thenReturn(Optional.empty());

    // ACT & ASSERT (En Junit 5, verificar excepciones se hace todo junto)
    // assertThrows captura la excepción y verifica que sea del tipo correcto
    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
        proveedorService.buscarPorId(99);
    });

    // Opcional: Verificas que el mensaje de error de la excepción sea el que tú escribiste
    assertEquals("Proveedor no encontrado", exception.getMessage());

    }

    @Test
void guardarProveedor_guardaYRetornaDTO(){
    // ARRANGE 
    when(proveedorRepository.save(proveEjemplo)).thenReturn(proveEjemplo);

    //ACT
    ProveedorDTO resultado = proveedorService.guardarProveedor(proveEjemplo);

    //ASSERT
    assertNotNull(resultado);
    assertEquals("distribuidora-HH", resultado.getNombre());
    verify(proveedorRepository).save(proveEjemplo);
}
    @Test
void guardarProveedor_inicializaFacturas_CuadoSonNull(){
    // ARRANGE preparo el escenario con el proveedor null
   proveEjemplo.setFacturas(null);
        when(proveedorRepository.save(proveEjemplo)).thenReturn(proveEjemplo);

    //ACT traigo el meodo original del service 
    ProveedorDTO resultado = proveedorService.guardarProveedor(proveEjemplo);

    // ASSERT el service debe haber iniciado la lista antes de guardar
    assertNotNull(resultado);
    assertNotNull(resultado.getFacturas());


}
    @Test
void eliminarProveedor_llamaAlRepositorio(){
    // ACT 
    proveedorService.eliminarProveedor(1);

    //ASSERT
    verify(proveedorRepository).deleteById(1);

}

    @Test
void guardarFactura_guardaYRetornaFacturaDTO(){
    //ARRANGE
    when(facturaRepository.save(facturaEjemplo)).thenReturn(facturaEjemplo);
    //ACT
    FacturaDTO resultado = proveedorService.guardarFactura(facturaEjemplo);

    //ASSERT
    assertNotNull(resultado);
    assertEquals("F-001", resultado.getNumeroFactura());
    assertEquals(120000, resultado.getMontoTotal());
}
    @ParameterizedTest
    @ValueSource(strings = {"PENDIENTE","COMPLETADO","CANCELADO"})
void actualizarEstadoFactura_cambiaAEstadoValido(String estadoValido){
    //ARRANGE
    Factura factura = new Factura();
    factura.setId(1);
    factura.setNumeroFactura("F-001");
    factura.setMontoTotal(120000.0);
    factura.setEstadoPedido(estadoValido);
    factura.setFechaEmision(new Date());


     when(facturaRepository.findById(1)).thenReturn(Optional.of(facturaEjemplo));
     when(facturaRepository.save(facturaEjemplo)).thenReturn(facturaEjemplo);


    //ACT

    FacturaDTO resultado = proveedorService.actualizarEstadoFactura(1, estadoValido.toLowerCase());
    //ASSERT

    assertNotNull(resultado);
    assertEquals(estadoValido, resultado.getEstadoPedido());

}
   @Test
void actualizarEstadoFactura_retornaNull_CuandoFacturaNoExiste(){
    //ARRANGE
  when(facturaRepository.findById(99)).thenReturn(Optional.empty());

    //ACT
    FacturaDTO resultado = proveedorService.actualizarEstadoFactura(99, "PENDIENTE");
    
   
    //ASSERT
    assertNull(resultado);
}
    @Test
void actualizarEstadoFactura_lanzaExcepcion_CuandoEstadoNoEsValido(){
    //ARRANGE
    when(facturaRepository.findById(1)).thenReturn(Optional.of(facturaEjemplo));
    
    //ACT
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, ()->{
        proveedorService.actualizarEstadoFactura(1,"ENVIADO"); // ESTADO NO VALIDO

    });
    //ASSERT
      assertEquals("Estado no permitido: ENVIADO", exception.getMessage());
}




}

