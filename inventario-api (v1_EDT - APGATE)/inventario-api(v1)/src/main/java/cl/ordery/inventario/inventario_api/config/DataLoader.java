package cl.ordery.inventario.inventario_api.config;

import cl.ordery.inventario.inventario_api.model.Ingrediente;
import cl.ordery.inventario.inventario_api.repository.IngredienteRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initData(IngredienteRepository ingredienteRepo) {
        return args -> {
            // Lógica exacta del profesor para validar si la BD ya tiene registros
            if (ingredienteRepo.count() > 0) {
                System.out.println("No se insertaron datos porque la bd no esta vacia");
            } else {
                // Insumos básicos y súper genéricos para calzar con cualquier menú
                Ingrediente ing1 = new Ingrediente(null, "Carne de Vacuno", 50.0, "kg");
                Ingrediente ing2 = new Ingrediente(null, "Pechuga de Pollo", 40.0, "kg");
                Ingrediente ing3 = new Ingrediente(null, "Queso Mozzarella", 30.0, "kg");
                Ingrediente ing4 = new Ingrediente(null, "Tomates", 25.0, "kg");
                Ingrediente ing5 = new Ingrediente(null, "Lechuga", 60.0, "un");
                Ingrediente ing6 = new Ingrediente(null, "Pan de Hamburguesa", 150.0, "un");
                Ingrediente ing7 = new Ingrediente(null, "Papas", 80.0, "kg");
                Ingrediente ing8 = new Ingrediente(null, "Arroz", 45.0, "kg");
                Ingrediente ing9 = new Ingrediente(null, "Aceite de Cocina", 20.0, "lt");

                // Guardado explícito uno a uno como en la pizarra
                ingredienteRepo.save(ing1);
                ingredienteRepo.save(ing2);
                ingredienteRepo.save(ing3);
                ingredienteRepo.save(ing4);
                ingredienteRepo.save(ing5);
                ingredienteRepo.save(ing6);
                ingredienteRepo.save(ing7);
                ingredienteRepo.save(ing8);
                ingredienteRepo.save(ing9);

                System.out.println("Datos cargados con exito");
            }
        };
    }
}