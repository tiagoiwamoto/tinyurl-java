package io.tinylink.entrypoint.rest;

import io.tinylink.config.SecurityConfig;
import io.tinylink.config.TinylinkProperties;
import io.tinylink.core.entity.AppSettings;
import io.tinylink.core.entity.ShortLink;
import io.tinylink.core.mapper.LinkMapper;
import io.tinylink.core.repository.AppUserRepository;
import io.tinylink.core.service.JwtService;
import io.tinylink.core.usecase.SettingsUseCase;
import io.tinylink.core.usecase.ShortenLinkUseCase;
import io.tinylink.entrypoint.rest.dto.LinkResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LinkApiController.class)
@Import(SecurityConfig.class)
@EnableConfigurationProperties(TinylinkProperties.class)
class LinkApiControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    ShortenLinkUseCase shortenLinkUseCase;

    @MockitoBean
    SettingsUseCase settingsUseCase;

    @MockitoBean
    LinkMapper linkMapper;

    @MockitoBean
    JwtService jwtService;

    @MockitoBean
    AppUserRepository userRepository;

    @Test
    void createsShortLink() throws Exception {
        ShortLink link = new ShortLink();
        link.setCode("AbC123xYz9");
        when(shortenLinkUseCase.create("https://exemplo.com", null, null)).thenReturn(link);

        AppSettings settings = new AppSettings();
        settings.setBaseUrl("http://localhost:8081");
        when(settingsUseCase.current()).thenReturn(settings);
        when(linkMapper.toResponse(any(ShortLink.class), anyString()))
                .thenReturn(new LinkResponse("AbC123xYz9", "http://localhost:8081/AbC123xYz9", "https://exemplo.com", true));

        mvc.perform(post("/api/links")
                        .contentType("application/json")
                        .content("{\"url\":\"https://exemplo.com\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/AbC123xYz9"))
                .andExpect(jsonPath("$.shortUrl").value("http://localhost:8081/AbC123xYz9"));
    }

    @Test
    void rejectsBlankUrl() throws Exception {
        mvc.perform(post("/api/links")
                        .contentType("application/json")
                        .content("{\"url\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.violations").isArray());
    }
}
