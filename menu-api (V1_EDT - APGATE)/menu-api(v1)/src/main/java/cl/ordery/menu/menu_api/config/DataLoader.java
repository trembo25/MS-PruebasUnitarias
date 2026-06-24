package cl.ordery.menu.menu_api.config;

import cl.ordery.menu.menu_api.model.Categoria;
import cl.ordery.menu.menu_api.model.Plato;
import cl.ordery.menu.menu_api.repository.CategoriaRepository;
import cl.ordery.menu.menu_api.repository.PlatoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initData(CategoriaRepository categoriaRepo, PlatoRepository platoRepo) {
        return args -> {
            if (categoriaRepo.count() > 0 || platoRepo.count() > 0) {
                System.out.println("No se insertaron datos porque la bd no esta vacia");
            } else {
                // 1. Crear las Categorías base
                Categoria cat1 = new Categoria(null, "Hamburguesas", "Hamburguesas artesanales con ingredientes frescos", new ArrayList<>());
                Categoria cat2 = new Categoria(null, "Platos de Fondo", "Platos principales preparados al instante", new ArrayList<>());
                Categoria cat3 = new Categoria(null, "Acompañamientos", "Las mejores guarniciones para tu comida", new ArrayList<>());

                categoriaRepo.save(cat1);
                categoriaRepo.save(cat2);
                categoriaRepo.save(cat3);

                // 2. Crear los Platos 
                Plato p1 = new Plato(null, "Hamburguesa Italiana", 6500.0, true, cat1); // Usa: Carne, Pan, Tomate, Lechuga, Aceite
                Plato p2 = new Plato(null, "Hamburguesa con Queso", 6000.0, true, cat1); // Usa: Carne, Pan, Queso Cheddar/Mozzarella
                Plato p3 = new Plato(null, "Pollo con Arroz", 7500.0, true, cat2);        // Usa: Pechuga de Pollo, Arroz
                Plato p4 = new Plato(null, "Pollo con Papas Fritas", 7000.0, true, cat2); // Usa: Pechuga de Pollo, Papas, Aceite
                Plato p5 = new Plato(null, "Porción de Papas Fritas", 3500.0, true, cat3); // Usa: Papas, Aceite

                platoRepo.save(p1);
                platoRepo.save(p2);
                platoRepo.save(p3);
                platoRepo.save(p4);
                platoRepo.save(p5);

                System.out.println("Datos de menú cargados con exito");
            }
        };
    }
}