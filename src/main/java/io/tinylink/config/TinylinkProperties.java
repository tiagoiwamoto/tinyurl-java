package io.tinylink.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "tinylink")
public record TinylinkProperties(
        @DefaultValue("admin") String adminUser,
        @DefaultValue("admin") String adminPassword,
        @DefaultValue("change-me-change-me-change-me-32b!") String jwtSecret,
        @DefaultValue("1440") long jwtExpirationMinutes,
        @DefaultValue("http://localhost:8080") String baseUrl,
        @DefaultValue("") String frontendUrl,
        @DefaultValue("3") int refreshRateSeconds,
        @DefaultValue("10") int linkLength,
        @DefaultValue("10") int pageSize,
        @DefaultValue("") String adHtml) {

    public TinylinkProperties {
        if (baseUrl != null && !baseUrl.matches("(?i)^https?://.*")) {
            baseUrl = "https://" + baseUrl;
        }
        if (baseUrl != null && baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }
        if (frontendUrl == null || frontendUrl.isBlank()) {
            frontendUrl = baseUrl;
        } else {
            if (!frontendUrl.matches("(?i)^https?://.*")) {
                frontendUrl = "https://" + frontendUrl;
            }
            if (frontendUrl.endsWith("/")) {
                frontendUrl = frontendUrl.substring(0, frontendUrl.length() - 1);
            }
        }
    }
}
