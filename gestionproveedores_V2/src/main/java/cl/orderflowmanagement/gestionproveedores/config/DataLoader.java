package cl.orderflowmanagement.gestionproveedores.config;

import cl.orderflowmanagement.gestionproveedores.model.Factura;
import cl.orderflowmanagement.gestionproveedores.model.Proveedor;
import cl.orderflowmanagement.gestionproveedores.repository.FacturaRepository;
import cl.orderflowmanagement.gestionproveedores.repository.ProveedorRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Date;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initData(ProveedorRepository proveedorRepo, FacturaRepository facturaRepo) {
        return args -> {
            if (proveedorRepo.count() > 0) {
                System.out.println("Ya hay datos cargados.");
                return;
            }

            Proveedor prov = new Proveedor();
            prov.setRut("76.123.456-K");
            prov.setNombre("Distribuidora Alimentos S.A.");
            prov.setRazonSocial("Alimentos Sur");
            prov.setDireccion("Av. Matta 123");
            prov.setCorreo("ventas@alimentosur.cl");
            prov.setTelefono("+56912345678");
            proveedorRepo.save(prov);

            Factura f = new Factura();
            f.setNumeroFactura("FAC-001");
            f.setFechaEmision(new Date());
            f.setFechaVencimiento(new Date()); 
            f.setMontoNeto(100000.0);
            f.setIva(19000.0);
            f.setMontoTotal(119000.0);
            f.setEstadoPedido("PENDIENTE");
            f.setProveedor(prov);
            facturaRepo.save(f);

            System.out.println("Datos iniciales cargados con éxito.");
        };
    }
}