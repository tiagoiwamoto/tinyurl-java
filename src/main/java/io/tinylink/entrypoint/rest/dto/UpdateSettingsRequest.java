package io.tinylink.entrypoint.rest.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateSettingsRequest(
        @NotBlank @Pattern(regexp = "^https?://.*", message = "baseUrl deve começar com http:// ou https://") String baseUrl,
        @Min(0) @Max(3600) int refreshRateSeconds,
        @Min(4) @Max(64) int linkLength,
        @Min(1) @Max(500) int pageSize,
        @Size(max = 10000) String adHtml) {
}
