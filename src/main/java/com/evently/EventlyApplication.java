package com.evently;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot application class for Evently Event Management System.
 * 
 * Evently is a comprehensive event planning platform that provides:
 * - Event planning and management
 * - Vendor discovery and booking
 * - User authentication with JWT
 * - Role-based access control (Admin, Vendor, Planner)
 * 
 * This application was converted from Django to Spring Boot and provides
 * RESTful APIs for all event management operations.
 * 
 * @author Evently Team
 * @version 1.0
 * @since 2024
 */
@SpringBootApplication
public class EventlyApplication {

    public static void main(String[] args) {
        // Set system properties for better logging and performance
        System.setProperty("spring.output.ansi.enabled", "always");
        System.setProperty("logging.pattern.console", 
            "%clr(%d{yyyy-MM-dd HH:mm:ss.SSS}){faint} %clr(%5p) %clr(${PID:- }){magenta} %clr(---){faint} %clr([%15.15t]){faint} %clr(%-40.40logger{39}){cyan} %clr(:){faint} %m%n%wEx");
        
        // Run the Spring Boot application
        SpringApplication app = new SpringApplication(EventlyApplication.class);
        
        // Set default properties
        app.setDefaultProperties(java.util.Map.of(
            "spring.banner.location", "classpath:banner.txt",
            "management.endpoints.web.exposure.include", "health,info,metrics"
        ));
        
        app.run(args);
    }
}