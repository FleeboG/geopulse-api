package com.geopulse.geopulse_api.events;

import com.geopulse.geopulse_api.events.dto.EventCreateRequest;
import com.geopulse.geopulse_api.events.dto.EventResponse;
import com.geopulse.geopulse_api.zones.ZoneEntity;
import com.geopulse.geopulse_api.zones.ZoneRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        EventEntity event = new EventEntity();
        event.setUserEmail(userEmail);
        event.setLatitude(req.latitude());
        event.setLongitude(req.longitude());

        EventEntity saved = eventRepository.save(event);

        List<String> matchedZones = zoneRepository.findByUserEmailOrderByCreatedAtDesc(userEmail)
                .stream()
                .filter(zone -> distanceMeters(req.latitude(), req.longitude(), zone.getLatitude(), zone.getLongitude()) <= zone.getRadiusM())
                .map(ZoneEntity::getName)
                .toList();

        return new EventResponse(
                saved.getId(),
                saved.getLatitude(),
                saved.getLongitude(),
                saved.getCreatedAt(),
                !matchedZones.isEmpty(),
                matchedZones
        );
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