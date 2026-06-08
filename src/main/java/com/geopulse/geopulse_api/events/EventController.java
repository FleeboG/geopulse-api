package com.geopulse.geopulse_api.events;

import com.geopulse.geopulse_api.events.dto.EventCreateRequest;
import com.geopulse.geopulse_api.events.dto.EventResponse;
import com.geopulse.geopulse_api.common.ListResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    public ResponseEntity<EventResponse> ingest(
            Authentication authentication,
            @Valid @RequestBody EventCreateRequest req
    ) {
        EventResponse created = eventService.ingest(authentication.getName(), req);

        return ResponseEntity
                .created(URI.create("/api/v1/events/" + created.id()))
                .body(created);
    }

    @GetMapping
    public ListResponse<EventResponse> list(
            Authentication authentication,
            @RequestParam(defaultValue = "50") int limit
    ) {
        List<EventResponse> items = eventService.list(authentication.getName(), limit);
        return new ListResponse<>(items, items.size(), Math.max(1, Math.min(limit, 100)));
    }
}