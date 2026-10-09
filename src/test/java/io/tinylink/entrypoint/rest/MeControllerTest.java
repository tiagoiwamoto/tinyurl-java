package io.tinylink.entrypoint.rest;

import io.tinylink.config.SecurityConfig;
import io.tinylink.config.TinylinkProperties;
import io.tinylink.core.entity.AppSettings;
import io.tinylink.core.entity.AppUser;
import io.tinylink.core.entity.Role;
import io.tinylink.core.mapper.LinkMapper;
import io.tinylink.core.repository.AppUserRepository;
import io.tinylink.core.service.JwtService;
import io.tinylink.core.usecase.MyLinksUseCase;
import io.tinylink.core.usecase.SettingsUseCase;
import io.tinylink.core.usecase.ShortenLinkUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MeController.class)
@Import(SecurityConfig.class)
@EnableConfigurationProperties(TinylinkProperties.class)
class MeControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    MyLinksUseCase myLinksUseCase;

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

    private UsernamePasswordAuthenticationToken auth() {
        AppUser user = new AppUser();
        user.setId(7L);
        user.setUsername("alice");
        user.setRole(Role.USER);
        return new UsernamePasswordAuthenticationToken(
                user, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    void rejectsUnauthenticated() throws Exception {
        mvc.perform(get("/api/me/links")).andExpect(status().isUnauthorized());
    }

    @Test
    void listsOwnLinks() throws Exception {
        when(myLinksUseCase.list(any(AppUser.class), isNull(), any())).thenReturn(Page.empty());
        AppSettings settings = new AppSettings();
        settings.setBaseUrl("http://localhost:8081");
        when(settingsUseCase.current()).thenReturn(settings);

        mvc.perform(get("/api/me/links").with(authentication(auth())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());

        verify(myLinksUseCase).list(any(AppUser.class), isNull(), any());
    }

    @Test
    void deletesOwnLink() throws Exception {
        mvc.perform(delete("/api/me/links/42").with(authentication(auth())))
                .andExpect(status().isNoContent());

        verify(myLinksUseCase).delete(any(AppUser.class), eq(42L));
    }
}
