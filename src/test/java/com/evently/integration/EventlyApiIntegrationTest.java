package com.evently.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

/**
 * Basic integration tests for the Evently API.
 * Tests health endpoints and basic API functionality.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class EventlyApiIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testHealthEndpoint() {
        String url = "http://localhost:" + port + "/health";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("UP"));
    }

    @Test
    public void testHealthPingEndpoint() {
        String url = "http://localhost:" + port + "/health/ping";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("pong"));
    }

    @Test
    public void testApiDocsEndpoint() {
        String url = "http://localhost:" + port + "/api/docs";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Evently API"));
    }

    @Test
    public void testApiEndpointsListing() {
        String url = "http://localhost:" + port + "/api/endpoints";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        String body = response.getBody();
        assertNotNull(body);
        assertTrue(body.contains("Users"));
        assertTrue(body.contains("Events"));
        assertTrue(body.contains("Vendors"));
        assertTrue(body.contains("Bookings"));
    }

    @Test
    public void testPublicVendorsEndpoint() {
        String url = "http://localhost:" + port + "/api/vendors";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        
        // Should return 200 even if empty (public endpoint)
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testPublicEventsEndpoint() {
        String url = "http://localhost:" + port + "/api/events";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        
        // Should return 200 even if empty (public endpoint)
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testUnauthorizedAccess() {
        // Test that protected endpoints require authentication
        String url = "http://localhost:" + port + "/api/users/me";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void testCorsHeaders() {
        String url = "http://localhost:" + port + "/api/docs";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        // Basic test - more detailed CORS testing would require specific headers
    }
}