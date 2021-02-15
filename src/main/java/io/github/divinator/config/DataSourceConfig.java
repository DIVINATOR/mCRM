package io.github.divinator.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.data.relational.core.dialect.Dialect;
import org.springframework.data.relational.core.dialect.H2Dialect;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

/**
 * Класс описывает конфигурацию подключения к базе данных Postgresql
 */
@Configuration
@EnableJdbcRepositories("io.github.divinator.datasource.repository")
@PropertySource("classpath:application.properties")
public class DataSourceConfig extends AbstractJdbcConfiguration {

    private final String dbUrl;
    private final String dbUserName;
    private final String dbPassword;

    public DataSourceConfig(
            @Value(value = "${application.db.name:dbase}") final String dbName,
            @Value(value = "${application.db.url:}") final String dbUrl,
            @Value(value = "${application.db.user:user}") final String dbUserName,
            @Value(value = "${application.db.pass:123456}") final String dbPassword
    ){
        this.dbUrl = String.format("jdbc:h2:file:./%s%s;FILE_LOCK=SOCKET", dbUrl, dbName);
        this.dbUserName = dbUserName;
        this.dbPassword = dbPassword;
    }

    @Bean
    public Dialect jdbcDialect(NamedParameterJdbcOperations operations) {
        return H2Dialect.INSTANCE;
    }

    @Bean
    public DataSource dataSource() {
        final DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl(dbUrl);
        return dataSource;
    }

    @Bean
    NamedParameterJdbcOperations namedParameterJdbcOperations(DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    @Bean
    PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
