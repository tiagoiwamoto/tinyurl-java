package io.tinylink.entrypoint.rest;

import io.tinylink.config.TinylinkProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.net.URI;

@Controller
@RequiredArgsConstructor
public class LegacyRedirectController {

    private final TinylinkProperties properties;

    @GetMapping("/{code:[a-zA-Z0-9]+}")
    public ResponseEntity<Void> follow(@PathVariable String code) {
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(properties.frontendUrl() + "/" + code))
                .build();
    }
}
