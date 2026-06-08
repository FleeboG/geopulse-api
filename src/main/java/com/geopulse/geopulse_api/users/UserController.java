package com.geopulse.geopulse_api.users;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class UserController {

    @GetMapping("/api/v1/me")
    public Map<String, String> me(Authentication authentication) {
        return Map.of(
                "email", authentication.getName()
        );
    }
}