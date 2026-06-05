package com.geopulse.geopulse_api.zones.dto;

import jakarta.validation.constraints.*;

public record ZoneCreateRequest(
    @NotBlank @Size(max = 120) String name,

    @DecimalMin(value = "-90.0") @DecimalMax(value = "90.0")
    double latitude,

    @DecimalMin(value = "-180.0") @DecimalMax(value = "180.0")
    double longitude,

    @Positive
    double radiusM
) {}