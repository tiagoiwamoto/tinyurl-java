package io.tinylink.entrypoint.rest;

import io.tinylink.core.entity.AppSettings;
import io.tinylink.core.entity.ShortLink;
import io.tinylink.core.usecase.ResolveLinkUseCase;
import io.tinylink.core.usecase.SettingsUseCase;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.net.URI;

@Controller
@RequiredArgsConstructor
public class RedirectController {

    private final ResolveLinkUseCase resolveLinkUseCase;
    private final SettingsUseCase settingsUseCase;

    @GetMapping("/{code:[a-zA-Z0-9]+}")
    public Object follow(@PathVariable String code, HttpServletRequest request, Model model) {
        ShortLink link = resolveLinkUseCase.resolve(code, clientIp(request));

        if (!link.isShowSplash()) {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create(link.getFullUrl()))
                    .build();
        }

        AppSettings settings = settingsUseCase.current();
        model.addAttribute("fullUrl", link.getFullUrl());
        model.addAttribute("refreshRate", settings.getRefreshRateSeconds());
        model.addAttribute("adHtml", settings.getAdHtml());
        model.addAttribute("baseUrl", settings.getBaseUrl());
        model.addAttribute("totalLinks", resolveLinkUseCase.totalLinks());
        return "splash";
    }

    private String clientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
