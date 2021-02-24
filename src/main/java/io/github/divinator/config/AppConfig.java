package io.github.divinator.config;

import io.github.divinator.datasource.entity.SettingsEntity;
import io.github.divinator.service.SettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.Arrays;

@Configuration
@PropertySource("classpath:application.properties")
public class AppConfig {

    private final SettingsService settingsService;
    private final Logger LOG = LoggerFactory.getLogger(AppConfig.class);
    private final String loglevel;
    private final String dbZoneId;
    private final boolean historyScheduler;

    public AppConfig(
            @Value(value = "${logging.level.root:INFO}") final String loglevel,
            @Value(value = "${application.db.zone:Europe/Moscow}") String dbZoneId,
            @Value(value = "${application.scheduler.history:true}") boolean historyScheduler,
            SettingsService settingsService) {
        this.loglevel = loglevel;
        this.dbZoneId = dbZoneId;
        this.historyScheduler = historyScheduler;
        this.settingsService = settingsService;
        initializeDefaultSettings();
    }

    /**
     * Инициализация настроек по умолчанию в базу данных, в том числе из файла "application.properties".
     */
    private void initializeDefaultSettings() {
        if (settingsService.getSettings("application.db.zone") == null) {
            settingsService.setAllSettings(Arrays.asList(
                    new SettingsEntity("user.name", System.getProperty("user.name")),
                    new SettingsEntity("application.db.zone", dbZoneId),
                    new SettingsEntity("application.scheduler.history", historyScheduler)
            ));
            LOG.info("Настройки по умолчанию инициализированы.");
        }
    }

    /**
     * Метод возвращает уровень логирования.
     * <p>
     * Log level: TRACE,DEBUG,INFO,WARN,ERROR,FATAL,OFF;
     * </p>
     *
     * @return Уровень логирования
     */
    public String getLoglevel() {
        return loglevel;
    }
}
