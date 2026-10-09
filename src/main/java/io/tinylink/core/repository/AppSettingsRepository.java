package io.tinylink.core.repository;

import io.tinylink.core.entity.AppSettings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppSettingsRepository extends JpaRepository<AppSettings, Long> {
}
