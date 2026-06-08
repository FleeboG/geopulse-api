package com.geopulse.geopulse_api.common;

import java.util.List;

public record ListResponse<T>(
        List<T> items,
        int count,
        int limit
) {
}