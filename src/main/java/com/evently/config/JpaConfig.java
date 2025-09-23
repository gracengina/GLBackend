package com.evently.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * JPA Configuration for Django database compatibility.
 * Ensures proper entity scanning and repository configuration.
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.evently.repository")
@EntityScan(basePackages = "com.evently.model")
@EnableTransactionManagement
public class JpaConfig {
    
    // Additional JPA configuration can be added here if needed
    // Current setup uses application.properties for most settings
}