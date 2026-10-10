package io.tinylink.entrypoint.rest;

import io.tinylink.core.entity.AppSettings;
import io.tinylink.core.entity.ShortLink;
import io.tinylink.core.usecase.ResolveLinkUseCase;
import io.tinylink.core.usecase.SettingsUseCase;
import io.tinylink.entrypoint.rest.dto.ResolveResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/resolve")
@RequiredArgsConstructor
public class ResolveController {

    private final ResolveLinkUseCase resolveLinkUseCase;
    private final SettingsUseCase settingsUseCase;

    @GetMapping("/{code:[a-zA-Z0-9]+}")
    public ResolveResponse resolve(@PathVariable String code, HttpServletRequest request) {
        ShortLink link = resolveLinkUseCase.resolve(code, clientIp(request));
        AppSettings settings = settingsUseCase.current();
        return new ResolveResponse(
                link.getCode(),
                link.getFullUrl(),
                link.isShowSplash(),
                settings.getRefreshRateSeconds(),
                settings.getAdHtml());
    }

    private String clientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
