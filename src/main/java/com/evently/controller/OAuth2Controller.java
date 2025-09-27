package com.evently.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.evently.model.User;
import com.evently.security.JwtTokenProvider;
import com.evently.service.UserService;

/**
 * OAuth2 Controller for handling Google and Facebook authentication.
 */
@RestController
@RequestMapping("/oauth2")
public class OAuth2Controller {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    /**
     * Handle successful OAuth2 authentication and return JWT token.
     */
    @GetMapping("/success")
    public ResponseEntity<OAuth2Response> oauth2Success(@AuthenticationPrincipal OAuth2User oauth2User) {
        try {
            // Extract user info from OAuth2User
            String email = oauth2User.getAttribute("email");
            String name = oauth2User.getAttribute("name");
            String provider = determineProvider(oauth2User);
            
            if (email == null) {
                return ResponseEntity.badRequest()
                    .body(new OAuth2Response(null, null, null, null, null, "Email not provided by OAuth2 provider"));
            }

            // Check if user exists, create if not
            User user = userService.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setUsername(generateUsername(name, email));
                    newUser.setFirstName(extractFirstName(name));
                    newUser.setLastName(extractLastName(name));
                    newUser.setPassword(""); // OAuth2 users don't need passwords
                    newUser.setRole(User.UserRole.CUSTOMER); // Default role
                    return userService.createUser(newUser);
                });

            // Generate JWT token
            String token = jwtTokenProvider.generateToken(user.getUsername());

            OAuth2Response response = new OAuth2Response(
                token,
                "Bearer",
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                "OAuth2 login successful via " + provider
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(new OAuth2Response(null, null, null, null, null, "OAuth2 authentication failed"));
        }
    }

    private String determineProvider(OAuth2User oauth2User) {
        // Check attributes to determine provider
        if (oauth2User.getAttributes().containsKey("sub")) {
            return "Google";
        } else if (oauth2User.getAttributes().containsKey("id")) {
            return "Facebook";
        }
        return "Unknown";
    }

    private String generateUsername(String name, String email) {
        if (name != null && !name.trim().isEmpty()) {
            return name.toLowerCase().replaceAll("\\s+", "");
        }
        return email.split("@")[0];
    }

    private String extractFirstName(String fullName) {
        if (fullName == null) return "";
        String[] parts = fullName.trim().split("\\s+");
        return parts.length > 0 ? parts[0] : "";
    }

    private String extractLastName(String fullName) {
        if (fullName == null) return "";
        String[] parts = fullName.trim().split("\\s+");
        return parts.length > 1 ? String.join(" ", java.util.Arrays.copyOfRange(parts, 1, parts.length)) : "";
    }

    /**
     * OAuth2 Response DTO
     */
    public static class OAuth2Response {
        private String token;
        private String type;
        private Long id;
        private String username;
        private String email;
        private String message;

        public OAuth2Response(String token, String type, Long id, String username, String email, String message) {
            this.token = token;
            this.type = type;
            this.id = id;
            this.username = username;
            this.email = email;
            this.message = message;
        }

        // Getters and setters
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}