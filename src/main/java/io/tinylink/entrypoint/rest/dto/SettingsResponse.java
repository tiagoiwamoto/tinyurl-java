package io.tinylink.entrypoint.rest.dto;

public record SettingsResponse(
        String baseUrl,
        int refreshRateSeconds,
        int linkLength,
        int pageSize,
        String adHtml) {
}
