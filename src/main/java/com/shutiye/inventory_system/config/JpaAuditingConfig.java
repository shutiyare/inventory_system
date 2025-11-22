package com.shutiye.inventory_system.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Configuration class to enable JPA Auditing.
 * This enables automatic population of @CreatedDate and @LastModifiedDate fields
 * in BaseEntity and its subclasses.
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
    // Configuration class - no implementation needed
}

