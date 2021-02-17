package io.github.divinator.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.properties")
public class AppConfig {

    @Value("${logging.level.root:INFO}")
    private final String loglevel;

    public AppConfig(
            @Value(value = "${app.adm.name:owner}") final String name,
            @Value(value = "${app.adm.pass:Aa111111}") final String pass,
            @Value(value = "${logging.level.root:}") final String loglevel
    ) {
        this.loglevel = loglevel;
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
