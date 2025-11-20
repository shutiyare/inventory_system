package com.shutiye.inventory_system.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Application configuration class
 * Can be used for initialization tasks and bean configurations
 */
@Configuration
public class ApplicationConfig {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationConfig.class);

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            logger.info("=========================================");
            logger.info("Inventory System Application Started");
            logger.info("Java Version: {}", System.getProperty("java.version"));
            logger.info("Spring Boot Application is running...");
            logger.info("=========================================");
        };
    }
}

