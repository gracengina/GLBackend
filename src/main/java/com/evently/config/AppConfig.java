package com.evently.config;

import java.time.format.DateTimeFormatter;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/*
 Provides common beans and configuration for the application.
 */
@Configuration
public class AppConfig {

    /*
    REST template for external API calls.
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /*
    Object mapper with proper date/time handling.
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        
        // Register JavaTimeModule for proper LocalDateTime serialization
        mapper.registerModule(new JavaTimeModule());
        
        // Disable writing dates as timestamps
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        // Configure other settings
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        
        return mapper;
    }

    /*
     Date time formatter for API responses.
     */
    @Bean
    public DateTimeFormatter dateTimeFormatter() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    }

    /*
     Date formatter for API responses.
     */
    @Bean
    public DateTimeFormatter dateFormatter() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd");
    }

    /*
    Application properties configuration.
     */
    @ConfigurationProperties(prefix = "app")
    public static class AppProperties {
        
        private String version = "1.0.0";
        private String name = "Evently Backend";
        private String description = "Event Planning and Vendor Management System";
        
        private final Jwt jwt = new Jwt();
        private final Cors cors = new Cors();
        private final File file = new File();
        private final Mail mail = new Mail();

        // Getters and setters
        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Jwt getJwt() {
            return jwt;
        }

        public Cors getCors() {
            return cors;
        }

        public File getFile() {
            return file;
        }

        public Mail getMail() {
            return mail;
        }

        public static class Jwt {
            private String secret = "defaultSecretKeyForDevelopmentOnlyChangeInProduction";
            private long expirationMs = 86400000; // 24 hours
            private long refreshExpirationMs = 604800000; // 7 days

            // Getters and setters
            public String getSecret() {
                return secret;
            }

            public void setSecret(String secret) {
                this.secret = secret;
            }

            public long getExpirationMs() {
                return expirationMs;
            }

            public void setExpirationMs(long expirationMs) {
                this.expirationMs = expirationMs;
            }

            public long getRefreshExpirationMs() {
                return refreshExpirationMs;
            }

            public void setRefreshExpirationMs(long refreshExpirationMs) {
                this.refreshExpirationMs = refreshExpirationMs;
            }
        }

        public static class Cors {
            private String[] allowedOrigins = {"http://localhost:3000", "http://localhost:8080"};

            // Getters and setters
            public String[] getAllowedOrigins() {
                return allowedOrigins;
            }

            public void setAllowedOrigins(String[] allowedOrigins) {
                this.allowedOrigins = allowedOrigins;
            }
        }

        public static class File {
            private String uploadDir = "uploads";
            private String maxSize = "10MB";

            // Getters and setters
            public String getUploadDir() {
                return uploadDir;
            }

            public void setUploadDir(String uploadDir) {
                this.uploadDir = uploadDir;
            }

            public String getMaxSize() {
                return maxSize;
            }

            public void setMaxSize(String maxSize) {
                this.maxSize = maxSize;
            }
        }

        public static class Mail {
            private String from = "noreply@evently.com";
            private String fromName = "Evently";

            // Getters and setters
            public String getFrom() {
                return from;
            }

            public void setFrom(String from) {
                this.from = from;
            }

            public String getFromName() {
                return fromName;
            }

            public void setFromName(String fromName) {
                this.fromName = fromName;
            }
        }
    }
}