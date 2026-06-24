package cl.duoc.gestionmesas.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cl.duoc.gestionmesas.model.Mesa;
import cl.duoc.gestionmesas.repository.MesaRepository;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner iniData(MesaRepository repo) {
        return ars -> {

            if (repo.count() == 0) {
                repo.save(new Mesa(null, 1, 4, "disponible"));
                repo.save(new Mesa(null, 2, 2, "ocupada"));
                repo.save(new Mesa(null, 3, 7, "reservada"));
                repo.save(new Mesa(null, 4, 3, "disponible"));
                repo.save(new Mesa(null, 5, 4, "disponible"));
                repo.save(new Mesa(null, 6, 5, "disponible"));
                repo.save(new Mesa(null, 7, 5, "disponible"));
                repo.save(new Mesa(null, 8, 4, "ocupada"));
                repo.save(new Mesa(null, 9, 4, "deshabilitada"));
                repo.save(new Mesa(null, 10, 4, "reservada"));
                repo.save(new Mesa(null, 11, 4, "disponible"));
                repo.save(new Mesa(null, 12, 4, "disponible"));
                repo.save(new Mesa(null, 13, 4, "disponible"));
                repo.save(new Mesa(null, 14, 4, "disponible"));
                repo.save(new Mesa(null, 15, 4, "disponible"));
            }
        };
    }
}
