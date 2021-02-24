package io.github.divinator.datasource.repository;

import io.github.divinator.datasource.entity.SettingsEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SettingsRepository extends CrudRepository<SettingsEntity, Long> {

    SettingsEntity findByKey(String key);
}
