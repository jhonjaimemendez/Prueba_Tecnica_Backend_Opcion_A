package com.arkho.flotas.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class OpenApiConfig {
	
	@Bean
    OpenAPI API() {
        return new OpenAPI()
                .info(new Info().title("API Gestion de Flotas")
                        .description("API para gestión de solicitudes de inscripción de vehículosk")
                        .version("1.0")
                        .license(new License().name("Propietaria")))
                .externalDocs(new ExternalDocumentation()
                        .description("Jhon Jaime Mendez, jhonjaimemendezalandete@gmail.com")
                        .url("http://localhost"));
    }
	
	
}
