package com.madgarage.api.repository;

import com.madgarage.api.model.SystemSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SystemSettingRepository extends JpaRepository<SystemSetting, Long> {
    Optional<SystemSetting> findByConfigKey(String configKey);
}
