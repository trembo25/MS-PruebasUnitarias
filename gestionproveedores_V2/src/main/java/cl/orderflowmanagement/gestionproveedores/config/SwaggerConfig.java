package cl.orderflowmanagement.gestionproveedores.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
// IMPORTACIONES CORRECTAS PARA SWAGGER/OPENAPI 3
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Microservicios de gestion de Proveedores")
                        .version("V1")
                        .description("Documentación de proveedores del Restaurante"));
    }
}