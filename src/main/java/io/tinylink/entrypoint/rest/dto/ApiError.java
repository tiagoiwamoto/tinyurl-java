package io.tinylink.entrypoint.rest.dto;

import java.time.Instant;
import java.util.List;

public record ApiError(Instant timestamp, int status, String message, String path, List<String> violations) {

    public static ApiError of(int status, String message, String path) {
        return new ApiError(Instant.now(), status, message, path, List.of());
    }
}
