package com.evently.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
public class SecurityConfig {


@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthFilter) throws Exception {
http
.csrf(csrf -> csrf.disable())
.cors(Customizer.withDefaults())
.authorizeHttpRequests(auth -> auth
.requestMatchers("/", "/health", "/health/**", "/actuator/health", "/api/auth/**", "/swagger-ui/**", "/v3/api-docs/**", "/favicon.ico").permitAll()
.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
// optionally make /api/endpoints public if that's intended:
//.requestMatchers("/api/endpoints").permitAll()
.anyRequest().authenticated()
)
.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);


return http.build();
}


// Ignore static assets entirely at the web layer (bypass security filter chain)
@Bean
public org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer webSecurityCustomizer() {
return (web) -> web.ignoring().requestMatchers(
"/favicon.ico", "/assets/**", "/static/**", "/css/**", "/js/**", "/images/**"
);
}
}
}
