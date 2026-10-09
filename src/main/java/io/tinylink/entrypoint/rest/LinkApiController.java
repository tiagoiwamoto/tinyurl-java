package io.tinylink.entrypoint.rest;

import io.tinylink.core.entity.AppUser;
import io.tinylink.core.entity.ShortLink;
import io.tinylink.core.mapper.LinkMapper;
import io.tinylink.core.usecase.SettingsUseCase;
import io.tinylink.core.usecase.ShortenLinkUseCase;
import io.tinylink.entrypoint.rest.dto.CreateLinkRequest;
import io.tinylink.entrypoint.rest.dto.LinkResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/links")
@RequiredArgsConstructor
public class LinkApiController {

    private final ShortenLinkUseCase shortenLinkUseCase;
    private final SettingsUseCase settingsUseCase;
    private final LinkMapper linkMapper;

    @PostMapping
    public ResponseEntity<LinkResponse> create(Authentication authentication,
                                               @Valid @RequestBody CreateLinkRequest request) {
        AppUser owner = authentication != null && authentication.getPrincipal() instanceof AppUser user
                ? user
                : null;
        ShortLink link = shortenLinkUseCase.create(request.url(), request.showSplash(), owner);
        String baseUrl = settingsUseCase.current().getBaseUrl();
        return ResponseEntity
                .created(URI.create("/" + link.getCode()))
                .body(linkMapper.toResponse(link, baseUrl));
    }
}
