package io.tinylink.entrypoint.rest.dto;

import jakarta.validation.constraints.NotNull;

public record UpdateLinkRequest(@NotNull Boolean showSplash) {
}
