package com.geopulse.geopulse_api.zones;

import com.geopulse.geopulse_api.zones.dto.ZoneCreateRequest;
import com.geopulse.geopulse_api.zones.dto.ZoneResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

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

    @GetMapping("/{id}")
    public ZoneResponse get(Authentication authentication, @PathVariable UUID id) {
        return zoneService.get(authentication.getName(), id);
    }

    @PutMapping("/{id}")
    public ZoneResponse update(
            Authentication authentication,
            @PathVariable UUID id,
            @Valid @RequestBody ZoneCreateRequest req
    ) {
        return zoneService.update(authentication.getName(), id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(Authentication authentication, @PathVariable UUID id) {
        zoneService.delete(authentication.getName(), id);
        return ResponseEntity.noContent().build();
    }
}