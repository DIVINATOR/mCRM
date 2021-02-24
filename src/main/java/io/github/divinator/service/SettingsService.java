package io.github.divinator.service;

import io.github.divinator.datasource.entity.SettingsEntity;
import io.github.divinator.datasource.repository.SettingsRepository;
import org.springframework.stereotype.Service;

/**
 * Класс описывает сервис для работы с записью и чтением настроек приложения из базы данных.
 */
@Service
public class SettingsService {

    private final SettingsRepository settingsRepository;

    public SettingsService(SettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }

    /**
     * Метод возвращает настройку по ее названию.
     *
     * @param name Название настройки.
     * @return Настройка.
     */
    public SettingsEntity getSettings(String name) {
        return settingsRepository.findByKey(name);
    }

    /**
     * Метод добавляет либо изменяет настройку.
     *
     * @param settings Настройка.
     */
    public void setSettings(SettingsEntity settings) {
        settingsRepository.save(settings);
    }

    /**
     * Метод возвращает все настройки.
     *
     * @return Все настройки.
     */
    public Iterable<SettingsEntity> getAllSettings() {
        return settingsRepository.findAll();
    }

    /**
     * Метод сохраняет все настройки.
     *
     * @param entities Все настройки.
     */
    public void setAllSettings(Iterable<SettingsEntity> entities) {
        settingsRepository.saveAll(entities);
    }

    /**
     * Метод возвращает количество настроек.
     *
     * @return Количество настроек.
     */
    public long count() {
        return settingsRepository.count();
    }

    /**
     * Метод проверяет существует ли указанная настройка.
     *
     * @param settings Указанная настройка.
     * @return true В случае если настройка указана, false в противном случае.
     */
    public boolean contains(SettingsEntity settings) {
        return settingsRepository.findByKey(settings.getKey()) != null;
    }
}
