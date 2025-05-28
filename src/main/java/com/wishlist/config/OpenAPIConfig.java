package com.wishlist.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public io.swagger.v3.oas.models.OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Wishlist")
                        .description("API para gerenciamento de Wishlist de clientes")
                        .contact(new Contact()
                                .name("By Luiz Felipe Gonnelli Barbosa")
                                .email("fehhgonnelli@hotmail.com ; fehhgonnelli@gmail.com"))

                );
    }
}

