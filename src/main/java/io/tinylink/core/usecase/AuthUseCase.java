package io.tinylink.core.usecase;

import io.tinylink.core.entity.AppUser;
import io.tinylink.core.entity.Role;
import io.tinylink.core.error.UserAlreadyExistsException;
import io.tinylink.core.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthUseCase {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AppUser register(String username, String email, String rawPassword) {
        String name = username.trim();
        if (userRepository.existsByUsername(name)) {
            throw new UserAlreadyExistsException(name);
        }
        AppUser user = new AppUser();
        user.setUsername(name);
        user.setEmail(email == null || email.isBlank() ? null : email.trim());
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        user.setRole(Role.USER);
        user.setCreatedAt(Instant.now());
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public AppUser login(String username, String rawPassword) {
        AppUser user = userRepository.findByUsername(username.trim())
                .orElseThrow(() -> new BadCredentialsException("Credenciais inválidas"));
        if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            throw new BadCredentialsException("Credenciais inválidas");
        }
        return user;
    }
}
