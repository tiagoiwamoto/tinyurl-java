package io.tinylink.entrypoint.rest;

import io.tinylink.core.entity.AppSettings;
import io.tinylink.core.entity.ShortLink;
import io.tinylink.core.mapper.LinkMapper;
import io.tinylink.core.usecase.AdminLinksUseCase;
import io.tinylink.core.usecase.SettingsUseCase;
import io.tinylink.core.usecase.StatsUseCase;
import io.tinylink.entrypoint.rest.dto.AdminLinkResponse;
import io.tinylink.entrypoint.rest.dto.PageResponse;
import io.tinylink.entrypoint.rest.dto.SettingsResponse;
import io.tinylink.entrypoint.rest.dto.StatsResponse;
import io.tinylink.entrypoint.rest.dto.UpdateLinkRequest;
import io.tinylink.entrypoint.rest.dto.UpdateSettingsRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminLinksUseCase adminLinksUseCase;
    private final SettingsUseCase settingsUseCase;
    private final StatsUseCase statsUseCase;
    private final LinkMapper linkMapper;

    @GetMapping("/links")
    public PageResponse<AdminLinkResponse> list(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        String baseUrl = settingsUseCase.current().getBaseUrl();
        Page<ShortLink> page = adminLinksUseCase.list(search, pageable);
        return PageResponse.of(page, link -> linkMapper.toAdminResponse(link, baseUrl));
    }

    @DeleteMapping("/links/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        adminLinksUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/links/{id}")
    public AdminLinkResponse update(@PathVariable Long id, @Valid @RequestBody UpdateLinkRequest request) {
        ShortLink link = adminLinksUseCase.updateSplash(id, request.showSplash());
        return linkMapper.toAdminResponse(link, settingsUseCase.current().getBaseUrl());
    }

    @GetMapping("/settings")
    public SettingsResponse settings() {
        return linkMapper.toResponse(settingsUseCase.current());
    }

    @PutMapping("/settings")
    public SettingsResponse updateSettings(@Valid @RequestBody UpdateSettingsRequest request) {
        AppSettings settings = settingsUseCase.update(
                request.baseUrl(),
                request.refreshRateSeconds(),
                request.linkLength(),
                request.pageSize(),
                request.adHtml());
        return linkMapper.toResponse(settings);
    }

    @GetMapping("/stats")
    public StatsResponse stats() {
        StatsUseCase.LinkStats stats = statsUseCase.current();
        String baseUrl = settingsUseCase.current().getBaseUrl();
        List<AdminLinkResponse> top = stats.topLinks().stream()
                .map(link -> linkMapper.toAdminResponse(link, baseUrl))
                .toList();
        List<StatsResponse.RecentHit> recent = stats.recentHits().stream()
                .map(hit -> new StatsResponse.RecentHit(hit.getId(), hit.getLink().getCode(), hit.getHitAt(), hit.getVisitorIp()))
                .toList();
        return new StatsResponse(stats.totalLinks(), stats.totalHits(), top, recent);
    }
}
