package com.geopulse.geopulse_api.zones;

import com.geopulse.geopulse_api.zones.dto.ZoneCreateRequest;
import com.geopulse.geopulse_api.zones.dto.ZoneResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ZoneService {

    private final ZoneRepository zoneRepository;

    public ZoneService(ZoneRepository zoneRepository) {
        this.zoneRepository = zoneRepository;
    }

    @Transactional
    public ZoneResponse create(String userEmail, ZoneCreateRequest req) {
        ZoneEntity zone = new ZoneEntity();
        zone.setUserEmail(userEmail);
        zone.setName(req.name().trim());
        zone.setLatitude(req.latitude());
        zone.setLongitude(req.longitude());
        zone.setRadiusM(req.radiusM());

        return toResponse(zoneRepository.save(zone));
    }

    @Transactional(readOnly = true)
    public List<ZoneResponse> list(String userEmail) {
        return zoneRepository.findByUserEmailOrderByCreatedAtDesc(userEmail)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ZoneResponse get(String userEmail, UUID id) {
        ZoneEntity zone = findOwnedZone(userEmail, id);
        return toResponse(zone);
    }

    @Transactional
    public ZoneResponse update(String userEmail, UUID id, ZoneCreateRequest req) {
        ZoneEntity zone = findOwnedZone(userEmail, id);

        zone.setName(req.name().trim());
        zone.setLatitude(req.latitude());
        zone.setLongitude(req.longitude());
        zone.setRadiusM(req.radiusM());

        return toResponse(zoneRepository.save(zone));
    }

    @Transactional
    public void delete(String userEmail, UUID id) {
        ZoneEntity zone = findOwnedZone(userEmail, id);
        zoneRepository.delete(zone);
    }

    private ZoneEntity findOwnedZone(String userEmail, UUID id) {
        return zoneRepository.findByIdAndUserEmail(id, userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Zone not found"));
    }

    private ZoneResponse toResponse(ZoneEntity zone) {
        return new ZoneResponse(
                zone.getId(),
                zone.getName(),
                zone.getLatitude(),
                zone.getLongitude(),
                zone.getRadiusM(),
                zone.getCreatedAt()
        );
    }
}