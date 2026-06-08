package com.geopulse.geopulse_api.common;

import java.time.OffsetDateTime;

public record ApiError(
        String error,
        String message,
        OffsetDateTime timestamp
) {}