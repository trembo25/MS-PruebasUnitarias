package cl.ordery.menu.menu_api.config;

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
                        .title("Microservicio de Gestión de Menú")
                        .version("1.0")
                        .description("Documentación de los endpoints para la administración del catálogo, categorías y platos del restaurante."));
    }
}
