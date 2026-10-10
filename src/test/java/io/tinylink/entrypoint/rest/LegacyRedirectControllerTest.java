package io.tinylink.entrypoint.rest;

import io.tinylink.config.SecurityConfig;
import io.tinylink.config.TinylinkProperties;
import io.tinylink.core.entity.AppSettings;
import io.tinylink.core.repository.AppUserRepository;
import io.tinylink.core.service.JwtService;
import io.tinylink.core.usecase.SettingsUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LegacyRedirectController.class)
@Import(SecurityConfig.class)
@EnableConfigurationProperties(TinylinkProperties.class)
class LegacyRedirectControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    SettingsUseCase settingsUseCase;

    @MockitoBean
    JwtService jwtService;

    @MockitoBean
    AppUserRepository userRepository;

    @Test
    void redirectsToFrontendDomain() throws Exception {
        AppSettings settings = new AppSettings();
        settings.setBaseUrl("https://encurtador-link.kamehouse.com.br");
        when(settingsUseCase.current()).thenReturn(settings);

        mvc.perform(get("/bXFJqzNhov"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location",
                        "https://encurtador-link.kamehouse.com.br/bXFJqzNhov"));
    }
}
