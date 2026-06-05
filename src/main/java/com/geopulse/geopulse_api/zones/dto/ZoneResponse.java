package com.geopulse.geopulse_api.zones.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ZoneResponse(
    UUID id,
    String name,
    double latitude,
    double longitude,
    double radiusM,
    OffsetDateTime createdAt
) {}