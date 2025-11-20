package com.shutiye.inventory_system.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI/Swagger configuration
 * Provides API documentation accessible at /swagger-ui.html
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI inventorySystemOpenAPI() {
        Server localServer = new Server();
        localServer.setUrl("http://localhost:8080");
        localServer.setDescription("Local Development Server");

        Contact contact = new Contact();
        contact.setName("Inventory System API");
        contact.setEmail("support@inventorysystem.com");

        License license = new License();
        license.setName("MIT License");
        license.setUrl("https://opensource.org/licenses/MIT");

        Info info = new Info();
        info.setTitle("Inventory System API");
        info.setVersion("1.0.0");
        info.setDescription("RESTful API for Inventory Management System. " +
                "This API provides endpoints for managing users and inventory items. " +
                "Built with Spring Boot 3.5.7 and Java 23.");
        info.setContact(contact);
        info.setLicense(license);

        OpenAPI openAPI = new OpenAPI();
        openAPI.setInfo(info);
        openAPI.setServers(List.of(localServer));
        
        return openAPI;
    }
}

