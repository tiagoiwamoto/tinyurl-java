package io.tinylink.entrypoint.rest.dto;

public record AuthResponse(String token, String username, String email, String role) {
}
