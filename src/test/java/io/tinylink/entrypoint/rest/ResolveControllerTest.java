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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ResolveController.class)
@Import(SecurityConfig.class)
@EnableConfigurationProperties(TinylinkProperties.class)
class ResolveControllerTest {

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

    @Test
    void resolvesLink() throws Exception {
        ShortLink link = new ShortLink();
        link.setCode("abc123");
        link.setFullUrl("http://destino.com");
        link.setShowSplash(true);
        when(resolveLinkUseCase.resolve(eq("abc123"), anyString())).thenReturn(link);

        AppSettings settings = new AppSettings();
        settings.setRefreshRateSeconds(3);
        settings.setAdHtml("<b>ad</b>");
        when(settingsUseCase.current()).thenReturn(settings);

        mvc.perform(get("/api/resolve/abc123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("abc123"))
                .andExpect(jsonPath("$.fullUrl").value("http://destino.com"))
                .andExpect(jsonPath("$.showSplash").value(true))
                .andExpect(jsonPath("$.refreshRateSeconds").value(3))
                .andExpect(jsonPath("$.adHtml").value("<b>ad</b>"));
    }

    @Test
    void resolvesImmediateRedirect() throws Exception {
        ShortLink link = new ShortLink();
        link.setCode("abc123");
        link.setFullUrl("http://destino.com");
        link.setShowSplash(false);
        when(resolveLinkUseCase.resolve(eq("abc123"), anyString())).thenReturn(link);

        AppSettings settings = new AppSettings();
        when(settingsUseCase.current()).thenReturn(settings);

        mvc.perform(get("/api/resolve/abc123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.showSplash").value(false));
    }
}
