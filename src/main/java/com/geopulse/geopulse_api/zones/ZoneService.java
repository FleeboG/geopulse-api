package com.geopulse.geopulse_api.zones;

import com.geopulse.geopulse_api.zones.dto.ZoneCreateRequest;
import com.geopulse.geopulse_api.zones.dto.ZoneResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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