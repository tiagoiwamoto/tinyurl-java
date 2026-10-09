package io.tinylink.entrypoint.rest;

import io.tinylink.core.entity.AppUser;
import io.tinylink.core.service.JwtService;
import io.tinylink.core.usecase.AuthUseCase;
import io.tinylink.entrypoint.rest.dto.AuthResponse;
import io.tinylink.entrypoint.rest.dto.LoginRequest;
import io.tinylink.entrypoint.rest.dto.RegisterRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthUseCase authUseCase;
    private final JwtService jwtService;

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        AppUser user = authUseCase.register(request.username(), request.email(), request.password());
        return toResponse(user);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        AppUser user = authUseCase.login(request.username(), request.password());
        return toResponse(user);
    }

    private AuthResponse toResponse(AppUser user) {
        return new AuthResponse(jwtService.issue(user), user.getUsername(), user.getEmail(), user.getRole().name());
    }
}
