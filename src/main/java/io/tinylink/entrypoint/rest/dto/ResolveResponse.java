package io.tinylink.entrypoint.rest.dto;

public record ResolveResponse(
        String code,
        String fullUrl,
        boolean showSplash,
        int refreshRateSeconds,
        String adHtml) {
}
