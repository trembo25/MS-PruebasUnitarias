package cl.ordery.cocina.gestioncocina.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Microservicio de Gestión de Cocina")
                        .version("1.0")
                        .description("Documentación de los endpoints para el control de órdenes, estados de preparación y flujo de trabajo de los chefs en el restaurante."));
    }
}