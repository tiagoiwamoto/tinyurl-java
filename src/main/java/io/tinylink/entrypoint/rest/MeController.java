package io.tinylink.entrypoint.rest;

import io.tinylink.core.entity.AppUser;
import io.tinylink.core.entity.ShortLink;
import io.tinylink.core.mapper.LinkMapper;
import io.tinylink.core.usecase.MyLinksUseCase;
import io.tinylink.core.usecase.SettingsUseCase;
import io.tinylink.core.usecase.ShortenLinkUseCase;
import io.tinylink.core.usecase.StatsUseCase;
import io.tinylink.entrypoint.rest.dto.AdminLinkResponse;
import io.tinylink.entrypoint.rest.dto.CreateLinkRequest;
import io.tinylink.entrypoint.rest.dto.LinkResponse;
import io.tinylink.entrypoint.rest.dto.PageResponse;
import io.tinylink.entrypoint.rest.dto.StatsResponse;
import io.tinylink.entrypoint.rest.dto.UpdateLinkRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
public class MeController {

    private final MyLinksUseCase myLinksUseCase;
    private final ShortenLinkUseCase shortenLinkUseCase;
    private final SettingsUseCase settingsUseCase;
    private final LinkMapper linkMapper;

    @GetMapping("/links")
    public PageResponse<AdminLinkResponse> list(
            Authentication authentication,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        String baseUrl = settingsUseCase.current().getBaseUrl();
        Page<ShortLink> page = myLinksUseCase.list(owner(authentication), search, pageable);
        return PageResponse.of(page, link -> linkMapper.toAdminResponse(link, baseUrl));
    }

    @PostMapping("/links")
    public ResponseEntity<LinkResponse> create(Authentication authentication,
                                               @Valid @RequestBody CreateLinkRequest request) {
        ShortLink link = shortenLinkUseCase.create(request.url(), request.showSplash(), owner(authentication));
        String baseUrl = settingsUseCase.current().getBaseUrl();
        return ResponseEntity
                .created(URI.create("/" + link.getCode()))
                .body(linkMapper.toResponse(link, baseUrl));
    }

    @PatchMapping("/links/{id}")
    public AdminLinkResponse update(Authentication authentication,
                                    @PathVariable Long id,
                                    @Valid @RequestBody UpdateLinkRequest request) {
        ShortLink link = myLinksUseCase.updateSplash(owner(authentication), id, request.showSplash());
        return linkMapper.toAdminResponse(link, settingsUseCase.current().getBaseUrl());
    }

    @DeleteMapping("/links/{id}")
    public ResponseEntity<Void> delete(Authentication authentication, @PathVariable Long id) {
        myLinksUseCase.delete(owner(authentication), id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats")
    public StatsResponse stats(Authentication authentication) {
        AppUser owner = owner(authentication);
        StatsUseCase.LinkStats stats = myLinksUseCase.stats(owner);
        String baseUrl = settingsUseCase.current().getBaseUrl();
        List<AdminLinkResponse> top = stats.topLinks().stream()
                .map(link -> linkMapper.toAdminResponse(link, baseUrl))
                .toList();
        List<StatsResponse.RecentHit> recent = stats.recentHits().stream()
                .map(hit -> new StatsResponse.RecentHit(hit.getId(), hit.getLink().getCode(), hit.getHitAt(), hit.getVisitorIp()))
                .toList();
        return new StatsResponse(stats.totalLinks(), stats.totalHits(), top, recent);
    }

    private AppUser owner(Authentication authentication) {
        return (AppUser) authentication.getPrincipal();
    }
}
