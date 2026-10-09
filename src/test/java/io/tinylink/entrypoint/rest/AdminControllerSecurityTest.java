package io.tinylink.entrypoint.rest;

import io.tinylink.config.SecurityConfig;
import io.tinylink.config.TinylinkProperties;
import io.tinylink.core.entity.AppSettings;
import io.tinylink.core.mapper.LinkMapper;
import io.tinylink.core.repository.AppUserRepository;
import io.tinylink.core.service.JwtService;
import io.tinylink.core.usecase.AdminLinksUseCase;
import io.tinylink.core.usecase.SettingsUseCase;
import io.tinylink.core.usecase.StatsUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
@Import(SecurityConfig.class)
@EnableConfigurationProperties(TinylinkProperties.class)
class AdminControllerSecurityTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    AdminLinksUseCase adminLinksUseCase;

    @MockitoBean
    SettingsUseCase settingsUseCase;

    @MockitoBean
    StatsUseCase statsUseCase;

    @MockitoBean
    LinkMapper linkMapper;

    @MockitoBean
    JwtService jwtService;

    @MockitoBean
    AppUserRepository userRepository;

    @Test
    void rejectsUnauthenticated() throws Exception {
        mvc.perform(get("/api/admin/links")).andExpect(status().isUnauthorized());
    }

    @Test
    void rejectsRegularUser() throws Exception {
        mvc.perform(get("/api/admin/links").with(user("alice").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void listsLinksWithAdminRole() throws Exception {
        when(adminLinksUseCase.list(isNull(), any())).thenReturn(Page.empty());
        AppSettings settings = new AppSettings();
        settings.setBaseUrl("http://localhost:8081");
        when(settingsUseCase.current()).thenReturn(settings);

        mvc.perform(get("/api/admin/links").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }
}
