package com.evently.config;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.evently.security.JwtAuthenticationEntryPoint;
import com.evently.security.JwtAuthenticationFilter;

/**
 * Comprehensive Security Configuration.
 * Provides JWT authentication, authorization, CORS, and security headers.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Value("${app.cors.allowed-origins:http://localhost:3000,http://localhost:8080}")
    private String allowedOrigins;

    public SecurityConfig(UserDetailsService userDetailsService, 
                         PasswordEncoder passwordEncoder,
                         JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                         JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(exceptions -> exceptions.authenticationEntryPoint(jwtAuthenticationEntryPoint))
            .authorizeHttpRequests(authz -> authz
                // Authentication endpoints - Must come first for precedence
                .requestMatchers("/auth/register", "/auth/login").permitAll()
                .requestMatchers("/api/users/register", "/api/users/login").permitAll()
                // Public endpoints
                .requestMatchers(HttpMethod.GET, "/health", "/actuator/health").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/docs", "/api/documentation/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/users/verify-email/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/vendors", "/api/vendors/{id}", "/api/vendors/search").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/vendors/{vendorId}/services", "/api/vendors/{vendorId}/portfolio").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/vendors/{vendorId}/reviews").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/events", "/api/events/{id}", "/api/events/search").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/events/upcoming", "/api/events/by-location").permitAll()
                // Admin endpoints
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/users/{id}/role").hasRole("ADMIN")
                // Vendor endpoints
                .requestMatchers(HttpMethod.POST, "/api/vendors/profile").hasRole("VENDOR")
                .requestMatchers(HttpMethod.PUT, "/api/vendors/{id}").hasRole("VENDOR")
                .requestMatchers(HttpMethod.POST, "/api/vendors/{vendorId}/services").hasRole("VENDOR")
                .requestMatchers(HttpMethod.PUT, "/api/vendors/services/{serviceId}").hasRole("VENDOR")
                .requestMatchers(HttpMethod.DELETE, "/api/vendors/services/{serviceId}").hasRole("VENDOR")
                .requestMatchers(HttpMethod.POST, "/api/vendors/{vendorId}/portfolio").hasRole("VENDOR")
                .requestMatchers(HttpMethod.PUT, "/api/vendors/portfolio/{itemId}").hasRole("VENDOR")
                .requestMatchers(HttpMethod.DELETE, "/api/vendors/portfolio/{itemId}").hasRole("VENDOR")
                .requestMatchers(HttpMethod.PUT, "/api/bookings/{id}/confirm", "/api/bookings/{id}/reject").hasRole("VENDOR")
                // Planner endpoints
                .requestMatchers(HttpMethod.POST, "/api/events").hasRole("PLANNER")
                .requestMatchers(HttpMethod.PUT, "/api/events/{id}").hasRole("PLANNER")
                .requestMatchers(HttpMethod.DELETE, "/api/events/{id}").hasRole("PLANNER")
                .requestMatchers(HttpMethod.POST, "/api/events/{id}/guests").hasRole("PLANNER")
                .requestMatchers(HttpMethod.PUT, "/api/events/{id}/guests/{guestId}").hasRole("PLANNER")
                .requestMatchers(HttpMethod.DELETE, "/api/events/{id}/guests/{guestId}").hasRole("PLANNER")
                .requestMatchers(HttpMethod.POST, "/api/bookings").hasRole("PLANNER")
                .requestMatchers(HttpMethod.PUT, "/api/bookings/{id}").hasRole("PLANNER")
                .requestMatchers(HttpMethod.DELETE, "/api/bookings/{id}").hasRole("PLANNER")
                // All other endpoints require authentication
                .anyRequest().authenticated()
            )
            .headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.deny())
                .contentTypeOptions(contentTypeOptions -> {})
                .httpStrictTransportSecurity(hstsConfig -> hstsConfig
                    .maxAgeInSeconds(31536000)
                    .includeSubDomains(true)
                )
                .referrerPolicy(referrerPolicy -> 
                    referrerPolicy.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
                )
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Split allowedOrigins property by commas
        List<String> origins = new ArrayList<>();
        if (allowedOrigins != null && !allowedOrigins.isBlank()) {
            origins.addAll(Arrays.asList(allowedOrigins.split("\\s*,\\s*")));
        }

        // Add wildcard local network patterns
        origins.add("http://192.168.*:*");
        origins.add("http://10.0.*:*");
        origins.add("http://172.16.*:*");
        
        // Add common development patterns
        origins.add("http://localhost:*");
        origins.add("http://127.0.0.1:*");

        configuration.setAllowedOriginPatterns(origins);

        configuration.setAllowedMethods(Arrays.asList(
            HttpMethod.GET.name(),
            HttpMethod.POST.name(),
            HttpMethod.PUT.name(),
            HttpMethod.DELETE.name(),
            HttpMethod.OPTIONS.name()
        ));

        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "X-Requested-With",
            "Accept",
            "Origin",
            "Access-Control-Request-Method",
            "Access-Control-Request-Headers"
        ));

        configuration.setExposedHeaders(Arrays.asList(
            "Access-Control-Allow-Origin",
            "Access-Control-Allow-Credentials",
            "Authorization"
        ));

        configuration.setAllowCredentials(true);
        configuration.setMaxAge(Duration.ofHours(1));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
