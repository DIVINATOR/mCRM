package io.github.divinator.config;

import io.github.divinator.datasource.entity.SettingsEntity;
import io.github.divinator.service.SettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.util.Arrays;

@Configuration
@PropertySource("classpath:application.properties")
public class AppConfig {

    private final Logger LOG = LoggerFactory.getLogger(AppConfig.class);

    public AppConfig(
            @Value(value = "${user.name}") String username,
            @Value(value = "${logging.level.root:INFO}") String loglevel,
            @Value(value = "${application.db.zone:Europe/Moscow}") String dbZoneId,
            @Value(value = "${application.calllog.follow:true}") boolean follow,
            @Value(value = "${application.shared.export:false}") boolean shared,
            SettingsService settingsService)
    {
        if(settingsService.count() == 0) {
            settingsService.setAllSettings(Arrays.asList(
                    new SettingsEntity("user.name", username),
                    new SettingsEntity("application.db.zone", dbZoneId),
                    new SettingsEntity("application.zone", ZoneId.systemDefault().getId()),
                    new SettingsEntity("application.calllog.follow", follow),
                    new SettingsEntity("logging.level.root", loglevel),
                    new SettingsEntity("application.shared.export", shared),
                    new SettingsEntity("application.shared.export.path", getSharedExportFile().toString())
            ));
        }
    }

    private Path getSharedExportFile() {
        return Paths.get(
                "\\\\Tambov2.ca.sbrf.ru",
                "vol2",
                "OITPT_PVB",
                "Контроль качества",
                "NICE",
                "Выгрузка"
                ).resolve("minicrm-export.csv");
    }
}
