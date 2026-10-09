package io.tinylink.config;

import io.tinylink.core.entity.AppUser;
import io.tinylink.core.entity.Role;
import io.tinylink.core.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class AdminUserSeeder implements ApplicationRunner {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TinylinkProperties properties;

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.existsByUsername(properties.adminUser())) {
            return;
        }
        AppUser admin = new AppUser();
        admin.setUsername(properties.adminUser());
        admin.setPasswordHash(passwordEncoder.encode(properties.adminPassword()));
        admin.setRole(Role.ADMIN);
        admin.setCreatedAt(Instant.now());
        userRepository.save(admin);
    }
}
