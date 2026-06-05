package com.geopulse.geopulse_api.events.dto;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record EventResponse(
    UUID id,
    double latitude,
    double longitude,
    OffsetDateTime createdAt,
    boolean insideAnyZone,
    List<String> matchedZones
) {}