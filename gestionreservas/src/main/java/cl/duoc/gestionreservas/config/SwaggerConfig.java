package cl.duoc.gestionreservas.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(new Info()
                                .title("Microservicio de Gestión de Reservas")
                                .version("v1.0")
                                .description("Documentación de la API de reservas del sistema de Gestión de Restaurante"));
    }
}
