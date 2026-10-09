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
        @DefaultValue("3") int refreshRateSeconds,
        @DefaultValue("10") int linkLength,
        @DefaultValue("10") int pageSize,
        @DefaultValue("") String adHtml) {
}
