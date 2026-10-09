package io.tinylink.entrypoint.rest.dto;

public record LinkResponse(String code, String shortUrl, String fullUrl, boolean showSplash) {
}
