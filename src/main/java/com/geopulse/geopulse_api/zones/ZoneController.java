package com.geopulse.geopulse_api.zones;

import com.geopulse.geopulse_api.zones.dto.ZoneCreateRequest;
import com.geopulse.geopulse_api.zones.dto.ZoneResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/zones")
public class ZoneController {

    private final ZoneService zoneService;

    public ZoneController(ZoneService zoneService) {
        this.zoneService = zoneService;
    }

    @PostMapping
    public ResponseEntity<ZoneResponse> create(
            Authentication authentication,
            @Valid @RequestBody ZoneCreateRequest req
    ) {
        ZoneResponse created = zoneService.create(authentication.getName(), req);

        return ResponseEntity
                .created(URI.create("/api/v1/zones/" + created.id()))
                .body(created);
    }

    @GetMapping
    public List<ZoneResponse> list(Authentication authentication) {
        return zoneService.list(authentication.getName());
    }
}