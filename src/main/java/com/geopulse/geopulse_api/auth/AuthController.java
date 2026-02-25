package com.geopulse.geopulse_api.auth;

import com.geopulse.geopulse_api.auth.dto.AuthResponse;
import com.geopulse.geopulse_api.auth.dto.LoginRequest;
import com.geopulse.geopulse_api.auth.dto.RegisterRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        try {
            authService.register(req);
            return ResponseEntity
                    .created(URI.create("/api/v1/auth/register"))
                    .body(Map.of("status", "registered"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of(
                            "error", "conflict",
                            "message", e.getMessage()
                    ));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        try {
            AuthResponse resp = authService.login(req);
            return ResponseEntity.ok(resp);
        } catch (IllegalArgumentException e) {
            // Invalid email/password
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "error", "unauthorized",
                            "message", "Invalid credentials"
                    ));
        }
    }
}