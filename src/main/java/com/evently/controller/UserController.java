package com.evently.controller;

import com.evently.security.JwtTokenProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for user authentication and token generation.
 */
@RestController
@RequestMapping("/api/auth")
public class UserController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public UserController(AuthenticationManager authenticationManager,
                          JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * Login endpoint that authenticates the user and returns a JWT token.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String username,
                                   @RequestParam String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            // Generate the JWT token using JwtTokenProvider
            String token = jwtTokenProvider.generateToken(authentication);

            // Optional: also return a refresh token if needed
            String refreshToken = jwtTokenProvider.generateRefreshToken(username);

            return ResponseEntity.ok().body(
                    new AuthResponse(token, refreshToken)
            );

        } catch (AuthenticationException ex) {
            return ResponseEntity.status(401)
                    .body("Invalid username or password");
        }
    }

    /**
     * Simple response object to hold the tokens.
     */
    public static class AuthResponse {
        private final String token;
        private final String refreshToken;

        public AuthResponse(String token, String refreshToken) {
            this.token = token;
            this.refreshToken = refreshToken;
        }

        public String getToken() {
            return token;
        }

        public String getRefreshToken() {
            return refreshToken;
        }
    }
}
