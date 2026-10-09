package io.tinylink.core.usecase;

import io.tinylink.config.TinylinkProperties;
import io.tinylink.core.entity.AppSettings;
import io.tinylink.core.repository.AppSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SettingsUseCase {

    private final AppSettingsRepository settingsRepository;
    private final TinylinkProperties defaults;

    @Transactional
    public AppSettings current() {
        AppSettings settings = settingsRepository.findById(AppSettings.SINGLETON_ID)
                .orElseGet(this::seedDefaults);
        String baseUrl = settings.getBaseUrl();
        if (baseUrl != null && !baseUrl.matches("(?i)^https?://.*")) {
            settings.setBaseUrl("https://" + baseUrl);
        }
        return settings;
    }

    @Transactional
    public AppSettings update(String baseUrl, int refreshRateSeconds, int linkLength, int pageSize, String adHtml) {
        AppSettings settings = current();
        settings.setBaseUrl(baseUrl);
        settings.setRefreshRateSeconds(refreshRateSeconds);
        settings.setLinkLength(linkLength);
        settings.setPageSize(pageSize);
        settings.setAdHtml(adHtml);
        return settingsRepository.save(settings);
    }

    private AppSettings seedDefaults() {
        AppSettings settings = new AppSettings();
        settings.setBaseUrl(defaults.baseUrl());
        settings.setRefreshRateSeconds(defaults.refreshRateSeconds());
        settings.setLinkLength(defaults.linkLength());
        settings.setPageSize(defaults.pageSize());
        settings.setAdHtml(defaults.adHtml());
        return settingsRepository.save(settings);
    }
}
