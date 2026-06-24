package cl.orderflowmanagement.gestionusuarios.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import cl.orderflowmanagement.gestionusuarios.model.RolEmpleado;
import cl.orderflowmanagement.gestionusuarios.repository.RolEmpleadoRepository;

@Component
public class DataInitializer implements CommandLineRunner{

    @Autowired
    private RolEmpleadoRepository rolEmpleadoRepository;

    @Override
    public void run(String... args) throws Exception {
        if (rolEmpleadoRepository.count() == 0) {
            rolEmpleadoRepository.save(new RolEmpleado(null, "admin"));
            rolEmpleadoRepository.save(new RolEmpleado(null, "garzon"));
            rolEmpleadoRepository.save(new RolEmpleado(null, "cocina"));
            rolEmpleadoRepository.save(new RolEmpleado(null, "recepcion"));

            System.out.println("Roles generados con exito e integrados en la BD");
        } else {
            System.out.println("Los roles no se pudieron generar con exito.");
        }
    }

}
