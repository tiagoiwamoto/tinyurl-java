package io.tinylink.entrypoint.rest;

import io.tinylink.config.SecurityConfig;
import io.tinylink.config.TinylinkProperties;
import io.tinylink.core.entity.AppUser;
import io.tinylink.core.entity.Role;
import io.tinylink.core.repository.AppUserRepository;
import io.tinylink.core.service.JwtService;
import io.tinylink.core.usecase.AuthUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
@EnableConfigurationProperties(TinylinkProperties.class)
class AuthControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    AuthUseCase authUseCase;

    @MockitoBean
    JwtService jwtService;

    @MockitoBean
    AppUserRepository userRepository;

    private AppUser user() {
        AppUser user = new AppUser();
        user.setId(1L);
        user.setUsername("alice");
        user.setEmail("alice@example.com");
        user.setRole(Role.USER);
        return user;
    }

    @Test
    void registersAndReturnsToken() throws Exception {
        when(authUseCase.register("alice", "alice@example.com", "segredo123")).thenReturn(user());
        when(jwtService.issue(org.mockito.ArgumentMatchers.any(AppUser.class))).thenReturn("jwt.token.aqui");

        mvc.perform(post("/api/auth/register")
                        .contentType("application/json")
                        .content("{\"username\":\"alice\",\"email\":\"alice@example.com\",\"password\":\"segredo123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt.token.aqui"))
                .andExpect(jsonPath("$.username").value("alice"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    void rejectsShortPassword() throws Exception {
        mvc.perform(post("/api/auth/register")
                        .contentType("application/json")
                        .content("{\"username\":\"alice\",\"password\":\"curta\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void logsInAndReturnsToken() throws Exception {
        when(authUseCase.login("alice", "segredo123")).thenReturn(user());
        when(jwtService.issue(org.mockito.ArgumentMatchers.any(AppUser.class))).thenReturn("jwt.token.aqui");

        mvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content("{\"username\":\"alice\",\"password\":\"segredo123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt.token.aqui"));
    }
}
