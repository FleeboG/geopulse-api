package com.geopulse.geopulse_api.auth;

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
            // simple foundation response; JWT/login comes next
            return ResponseEntity
                    .created(URI.create("/api/v1/auth/register"))
                    .body(Map.of("status", "registered"));
        } catch (IllegalArgumentException e) {
            // e.g. email already registered
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of(
                            "error", "conflict",
                            "message", e.getMessage()
                    ));
        }
    }
}