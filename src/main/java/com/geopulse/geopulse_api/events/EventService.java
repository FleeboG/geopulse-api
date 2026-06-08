package com.geopulse.geopulse_api.events;

import com.geopulse.geopulse_api.events.dto.EventCreateRequest;
import com.geopulse.geopulse_api.events.dto.EventResponse;
import com.geopulse.geopulse_api.zones.ZoneEntity;
import com.geopulse.geopulse_api.zones.ZoneRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;

import java.util.List;

@Service
public class EventService {

    private static final double EARTH_RADIUS_M = 6_371_000.0;

    private final EventRepository eventRepository;
    private final ZoneRepository zoneRepository;

    public EventService(EventRepository eventRepository, ZoneRepository zoneRepository) {
        this.eventRepository = eventRepository;
        this.zoneRepository = zoneRepository;
    }

    @Transactional
    public EventResponse ingest(String userEmail, EventCreateRequest req) {
        boolean wasInside = eventRepository.findFirstByUserEmailOrderByCreatedAtDesc(userEmail)
                .map(EventEntity::getMatchedZoneNames)
                .map(names -> names != null && !names.isBlank())
                .orElse(false);

        List<String> matchedZones = zoneRepository.findByUserEmailOrderByCreatedAtDesc(userEmail)
                .stream()
                .filter(zone -> distanceMeters(req.latitude(), req.longitude(), zone.getLatitude(), zone.getLongitude()) <= zone.getRadiusM())
                .map(ZoneEntity::getName)
                .toList();

        boolean isInside = !matchedZones.isEmpty();
        String eventType = determineEventType(wasInside, isInside);

        EventEntity event = new EventEntity();
        event.setUserEmail(userEmail);
        event.setLatitude(req.latitude());
        event.setLongitude(req.longitude());
        event.setEventType(eventType);
        event.setMatchedZoneNames(String.join(",", matchedZones));

        EventEntity saved = eventRepository.save(event);

        return new EventResponse(
            event.getId(),
            event.getLatitude(),
            event.getLongitude(),
            event.getCreatedAt(),
            isInside,
            matchedZones,
            saved.getEventType()
        );
    }

    @Transactional(readOnly = true)
    public List<EventResponse> list(String userEmail, int limit) {
        int safeLimit = Math.max(1, Math.min(limit, 100));

        return eventRepository.findByUserEmailOrderByCreatedAtDesc(
                        userEmail,
                        PageRequest.of(0, safeLimit)
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private EventResponse toResponse(EventEntity event) {
        List<String> matchedZones = event.getMatchedZoneNames() == null || event.getMatchedZoneNames().isBlank()
                ? List.of()
                : List.of(event.getMatchedZoneNames().split(","));

        boolean isInside = !matchedZones.isEmpty();

        return new EventResponse(
                event.getId(),
                event.getLatitude(),
                event.getLongitude(),
                event.getCreatedAt(),
                isInside,
                matchedZones,
                event.getEventType()
        );
    }

    private static String determineEventType(boolean wasInside, boolean isInside) {
        if (!wasInside && isInside) return "ENTER";
        if (wasInside && !isInside) return "EXIT";
        if (isInside) return "INSIDE";
        return "OUTSIDE";
    }

    private static double distanceMeters(double lat1, double lon1, double lat2, double lon2) {
        double lat1Rad = Math.toRadians(lat1);
        double lat2Rad = Math.toRadians(lat2);
        double deltaLat = Math.toRadians(lat2 - lat1);
        double deltaLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                + Math.cos(lat1Rad) * Math.cos(lat2Rad)
                * Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_M * c;
    }
}