package com.evently.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;

/**
 * Health Check Controller.
 * Provides health status information for the application.
 */
@RestController
@RequestMapping("/health")
public class HealthController {

    @Autowired
    private DataSource dataSource;

    @Value("${spring.application.name:evently-backend}")
    private String applicationName;

    @Value("${app.version:1.0.0}")
    private String version;

    private static final long startTime = System.currentTimeMillis();
    
    /**
     * Basic health check endpoint.
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        
        health.put("status", "UP");
        health.put("application", applicationName);
        health.put("version", version);
        health.put("timestamp", LocalDateTime.now());
        
        // Check database connectivity
        boolean dbConnected = checkDatabaseConnection();
        health.put("database", dbConnected ? "UP" : "DOWN");
        
        // Overall status
        if (!dbConnected) {
            health.put("status", "DOWN");
            return ResponseEntity.status(503).body(health);
        }
        
        return ResponseEntity.ok(health);
    }

    /**
     * Detailed health information.
     */
    @GetMapping("/detailed")
    public ResponseEntity<Map<String, Object>> detailedHealth() {
        Map<String, Object> health = new HashMap<>();
        
        // Basic info
        health.put("application", applicationName);
        health.put("version", version);
        health.put("timestamp", LocalDateTime.now());
        health.put("uptime", getUptime());
        
        // System info
        Map<String, Object> system = new HashMap<>();
        Runtime runtime = Runtime.getRuntime();
        system.put("totalMemory", runtime.totalMemory());
        system.put("freeMemory", runtime.freeMemory());
        system.put("usedMemory", runtime.totalMemory() - runtime.freeMemory());
        system.put("maxMemory", runtime.maxMemory());
        system.put("processors", runtime.availableProcessors());
        health.put("system", system);
        
        // Database info
        Map<String, Object> database = new HashMap<>();
        boolean dbConnected = checkDatabaseConnection();
        database.put("status", dbConnected ? "UP" : "DOWN");
        database.put("type", "MySQL");
        health.put("database", database);
        
        // Overall status
        health.put("status", dbConnected ? "UP" : "DOWN");
        
        if (!dbConnected) {
            return ResponseEntity.status(503).body(health);
        }
        
        return ResponseEntity.ok(health);
    }

    /**
     * Simple ping endpoint.
     */
    @GetMapping("/ping")
    public ResponseEntity<Map<String, String>> ping() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "pong");
        response.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }

    /**
     * Check database connection.
     */
    private boolean checkDatabaseConnection() {
        try {
            dataSource.getConnection().close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get application uptime 
     */
    private String getUptime() {
        long uptimeMs = System.currentTimeMillis() - startTime;
        long uptimeSeconds = uptimeMs / 1000;
        long hours = uptimeSeconds / 3600;
        long minutes = (uptimeSeconds % 3600) / 60;
        long seconds = uptimeSeconds % 60;
        
        return String.format("%d hours, %d minutes, %d seconds", hours, minutes, seconds);
    }
}
