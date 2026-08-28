package cl.smarttask.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration(proxyBeanMethods = false)
@Profile("dev")
public class OpenApiConfiguration {

    @Bean
    OpenAPI smartTaskOpenApi() {
        return new OpenAPI().info(new Info()
                .title("SmartTask API")
                .description("API REST para crear, consultar, completar y eliminar tareas")
                .version("v1")
                .contact(new Contact().name("SmartTask Team")));
    }
}
