package io.tinylink.entrypoint.rest;

import io.tinylink.config.SecurityConfig;
import io.tinylink.config.TinylinkProperties;
import io.tinylink.core.entity.AppSettings;
import io.tinylink.core.entity.ShortLink;
import io.tinylink.core.repository.AppUserRepository;
import io.tinylink.core.service.JwtService;
import io.tinylink.core.usecase.ResolveLinkUseCase;
import io.tinylink.core.usecase.SettingsUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(RedirectController.class)
@Import(SecurityConfig.class)
@EnableConfigurationProperties(TinylinkProperties.class)
class RedirectControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    ResolveLinkUseCase resolveLinkUseCase;

    @MockitoBean
    SettingsUseCase settingsUseCase;

    @MockitoBean
    JwtService jwtService;

    @MockitoBean
    AppUserRepository userRepository;

    private ShortLink link(boolean splash) {
        ShortLink link = new ShortLink();
        link.setCode("abc123");
        link.setFullUrl("http://destino.com");
        link.setShowSplash(splash);
        return link;
    }

    @Test
    void redirectsImmediatelyWhenSplashDisabled() throws Exception {
        when(resolveLinkUseCase.resolve(eq("abc123"), anyString())).thenReturn(link(false));

        mvc.perform(get("/abc123"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "http://destino.com"));
    }

    @Test
    void rendersSplashPageWhenEnabled() throws Exception {
        when(resolveLinkUseCase.resolve(eq("abc123"), anyString())).thenReturn(link(true));
        AppSettings settings = new AppSettings();
        settings.setBaseUrl("http://localhost:8081");
        settings.setRefreshRateSeconds(3);
        settings.setAdHtml("<b>ad</b>");
        when(settingsUseCase.current()).thenReturn(settings);
        when(resolveLinkUseCase.totalLinks()).thenReturn(7L);

        mvc.perform(get("/abc123"))
                .andExpect(status().isOk())
                .andExpect(view().name("splash"))
                .andExpect(model().attribute("fullUrl", "http://destino.com"))
                .andExpect(model().attribute("refreshRate", 3))
                .andExpect(model().attribute("totalLinks", 7L));
    }
}
