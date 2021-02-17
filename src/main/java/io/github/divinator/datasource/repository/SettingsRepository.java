package io.github.divinator.datasource.repository;

import io.github.divinator.datasource.entity.Settings;
import org.springframework.data.repository.CrudRepository;

public interface SettingsRepository extends CrudRepository<Settings, Long> {

}
