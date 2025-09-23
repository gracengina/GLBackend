package com.evently.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * API Documentation Controller.
 * Provides basic API documentation and endpoint listing.
 */
@RestController
@RequestMapping("/api")
public class ApiDocController {

    /**
     * Get API documentation overview.
     */
    @GetMapping("/docs")
    public ResponseEntity<Map<String, Object>> getApiDocs() {
        Map<String, Object> docs = new HashMap<>();
        
        docs.put("name", "Evently API");
        docs.put("version", "1.0.0");
        docs.put("description", "Event Planning and Vendor Management System API");
        docs.put("baseUrl", "/api");
        
        // Authentication info
        Map<String, String> auth = new HashMap<>();
        auth.put("type", "JWT Bearer Token");
        auth.put("header", "Authorization: Bearer <token>");
        auth.put("loginEndpoint", "POST /api/users/login");
        auth.put("registerEndpoint", "POST /api/users/register");
        docs.put("authentication", auth);
        
        // Endpoint categories
        docs.put("endpoints", getEndpointCategories());
        
        return ResponseEntity.ok(docs);
    }

    /**
     * Get available endpoints organized by category.
     */
    @GetMapping("/endpoints")
    public ResponseEntity<Map<String, List<Map<String, String>>>> getEndpoints() {
        return ResponseEntity.ok(getEndpointCategories());
    }

    /**
     * Build endpoint categories.
     */
    private Map<String, List<Map<String, String>>> getEndpointCategories() {
        Map<String, List<Map<String, String>>> categories = new HashMap<>();
        
        // User endpoints
        List<Map<String, String>> userEndpoints = new ArrayList<>();
        userEndpoints.add(createEndpoint("POST", "/api/users/register", "Register new user", "Public"));
        userEndpoints.add(createEndpoint("POST", "/api/users/login", "User login", "Public"));
        userEndpoints.add(createEndpoint("POST", "/api/users/logout", "User logout", "Authenticated"));
        userEndpoints.add(createEndpoint("GET", "/api/users/me", "Get current user profile", "Authenticated"));
        userEndpoints.add(createEndpoint("PUT", "/api/users/profile", "Update user profile", "Authenticated"));
        userEndpoints.add(createEndpoint("PUT", "/api/users/{id}/role", "Update user role", "Admin"));
        categories.put("Users", userEndpoints);
        
        // Event endpoints
        List<Map<String, String>> eventEndpoints = new ArrayList<>();
        eventEndpoints.add(createEndpoint("GET", "/api/events", "Get all events", "Public"));
        eventEndpoints.add(createEndpoint("GET", "/api/events/{id}", "Get event by ID", "Public"));
        eventEndpoints.add(createEndpoint("POST", "/api/events", "Create new event", "Planner"));
        eventEndpoints.add(createEndpoint("PUT", "/api/events/{id}", "Update event", "Planner"));
        eventEndpoints.add(createEndpoint("DELETE", "/api/events/{id}", "Delete event", "Planner"));
        eventEndpoints.add(createEndpoint("GET", "/api/events/my-events", "Get user's events", "Planner"));
        eventEndpoints.add(createEndpoint("GET", "/api/events/search", "Search events", "Public"));
        eventEndpoints.add(createEndpoint("POST", "/api/events/{id}/guests", "Add guest to event", "Planner"));
        categories.put("Events", eventEndpoints);
        
        // Vendor endpoints
        List<Map<String, String>> vendorEndpoints = new ArrayList<>();
        vendorEndpoints.add(createEndpoint("GET", "/api/vendors", "Get all vendors", "Public"));
        vendorEndpoints.add(createEndpoint("GET", "/api/vendors/{id}", "Get vendor by ID", "Public"));
        vendorEndpoints.add(createEndpoint("POST", "/api/vendors/profile", "Create vendor profile", "Vendor"));
        vendorEndpoints.add(createEndpoint("PUT", "/api/vendors/{id}", "Update vendor profile", "Vendor"));
        vendorEndpoints.add(createEndpoint("GET", "/api/vendors/my-profile", "Get current vendor profile", "Vendor"));
        vendorEndpoints.add(createEndpoint("GET", "/api/vendors/search", "Search vendors", "Public"));
        vendorEndpoints.add(createEndpoint("POST", "/api/vendors/{vendorId}/services", "Add service", "Vendor"));
        vendorEndpoints.add(createEndpoint("POST", "/api/vendors/{vendorId}/portfolio", "Add portfolio item", "Vendor"));
        categories.put("Vendors", vendorEndpoints);
        
        // Booking endpoints
        List<Map<String, String>> bookingEndpoints = new ArrayList<>();
        bookingEndpoints.add(createEndpoint("POST", "/api/bookings", "Create booking", "Planner"));
        bookingEndpoints.add(createEndpoint("GET", "/api/bookings/{id}", "Get booking by ID", "Authenticated"));
        bookingEndpoints.add(createEndpoint("PUT", "/api/bookings/{id}", "Update booking", "Authenticated"));
        bookingEndpoints.add(createEndpoint("DELETE", "/api/bookings/{id}", "Cancel booking", "Authenticated"));
        bookingEndpoints.add(createEndpoint("PUT", "/api/bookings/{id}/confirm", "Confirm booking", "Vendor"));
        bookingEndpoints.add(createEndpoint("GET", "/api/bookings/my-bookings", "Get user's bookings", "Planner"));
        bookingEndpoints.add(createEndpoint("GET", "/api/bookings/event/{eventId}", "Get bookings by event", "Authenticated"));
        bookingEndpoints.add(createEndpoint("GET", "/api/bookings/vendor/{vendorId}", "Get bookings by vendor", "Authenticated"));
        categories.put("Bookings", bookingEndpoints);
        
        // Health endpoints
        List<Map<String, String>> healthEndpoints = new ArrayList<>();
        healthEndpoints.add(createEndpoint("GET", "/health", "Basic health check", "Public"));
        healthEndpoints.add(createEndpoint("GET", "/health/detailed", "Detailed health info", "Public"));
        healthEndpoints.add(createEndpoint("GET", "/health/ping", "Simple ping", "Public"));
        categories.put("Health", healthEndpoints);
        
        return categories;
    }

    /**
     * Create endpoint info map.
     */
    private Map<String, String> createEndpoint(String method, String path, String description, String auth) {
        Map<String, String> endpoint = new HashMap<>();
        endpoint.put("method", method);
        endpoint.put("path", path);
        endpoint.put("description", description);
        endpoint.put("authentication", auth);
        return endpoint;
    }
}