package io.tinylink.entrypoint.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateLinkRequest(
        @NotBlank @Size(max = 2048) String url,
        Boolean showSplash) {
}
