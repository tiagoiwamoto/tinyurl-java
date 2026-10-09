package io.tinylink.entrypoint.rest.dto;

import java.time.Instant;

public record AdminLinkResponse(
        Long id,
        String code,
        String shortUrl,
        String fullUrl,
        String resolvedIp,
        boolean showSplash,
        long hitCount,
        Instant createdAt) {
}
