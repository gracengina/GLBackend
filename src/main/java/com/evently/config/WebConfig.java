package com.evently.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web Configuration.
 * Handles file upload directories and static resource serving.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(WebConfig.class);

    @Value("${app.file.upload-dir:uploads}")
    private String uploadDir;

    @Value("${app.cors.allowed-origins:http://localhost:3000,http://localhost:8080}")
    private String[] allowedOrigins;

    @Value("${app.cors.allowed-methods:GET,POST,PUT,DELETE,OPTIONS}")
    private String[] allowedMethods;

    @Value("${app.cors.allowed-headers:*}")
    private String allowedHeaders;

    @Value("${app.cors.allow-credentials:true}")
    private boolean allowCredentials;

    @Value("${app.cors.max-age:3600}")
    private long maxAge;

    /**
     * Configure static resource handling for uploaded files.
     */
    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        // Serve uploaded files
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadDir + "/")
                .setCachePeriod(86400); // Cache for 24 hours

        // Serve static assets
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(86400);
    }

    /**
     * Configure CORS for web MVC (additional to security CORS).
     * Allows frontend from ANY origin to access the API.
     * WARNING: This allows all domains - use with caution in production!
     */
    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        logger.info("Configuring CORS to allow ALL ORIGINS");
        
        // Allow ALL origins for all endpoints
        registry.addMapping("/**")
                .allowedOriginPatterns("*")  // Allow all origins
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
                
        // Specific mapping for API endpoints - allow all origins
        registry.addMapping("/api/**")
                .allowedOriginPatterns("*")  // Allow all origins
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);

        // Specific mapping for auth endpoints - allow all origins
        registry.addMapping("/auth/**")
                .allowedOriginPatterns("*")  // Allow all origins
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    /**
     * Create upload directory on application startup.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void createUploadDirectory() {
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                logger.info("Created upload directory: {}", uploadPath.toAbsolutePath());
            }

            // Create subdirectories for different file types
            createSubDirectory(uploadPath, "profiles");
            createSubDirectory(uploadPath, "events");
            createSubDirectory(uploadPath, "vendors");
            createSubDirectory(uploadPath, "portfolio");
            createSubDirectory(uploadPath, "temp");

        } catch (IOException e) {
            logger.error("Failed to create upload directory: {}", uploadDir, e);
            throw new RuntimeException("Could not create upload directory", e);
        }
    }

    /**
     * Create subdirectory if it doesn't exist.
     */
    private void createSubDirectory(Path parentPath, String subdirName) {
        try {
            Path subdirPath = parentPath.resolve(subdirName);
            if (!Files.exists(subdirPath)) {
                Files.createDirectories(subdirPath);
                logger.debug("Created subdirectory: {}", subdirPath);
            }
        } catch (IOException e) {
            logger.warn("Failed to create subdirectory: {}", subdirName, e);
        }
    }
}