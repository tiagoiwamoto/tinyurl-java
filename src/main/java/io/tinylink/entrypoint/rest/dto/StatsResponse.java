package io.tinylink.entrypoint.rest.dto;

import java.time.Instant;
import java.util.List;

public record StatsResponse(
        long totalLinks,
        long totalHits,
        List<AdminLinkResponse> topLinks,
        List<RecentHit> recentHits) {

    public record RecentHit(Long id, String code, Instant hitAt, String visitorIp) {
    }
}
