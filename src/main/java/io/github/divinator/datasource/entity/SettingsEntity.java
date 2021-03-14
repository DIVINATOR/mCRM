package io.github.divinator.datasource.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Сущность описывает настройку в таблице базы данных.
 */
@Table("t_settings")
public class SettingsEntity {

    @Id
    private long id;

    @Column("key")
    private final String key;

    @Column("value")
    private Object value;

    @PersistenceConstructor
    public SettingsEntity(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Метод получает ключ(название) настройки.
     *
     * @return Ключ(название) настройки.
     */
    public String getKey() {
        return key;
    }

    /**
     * Метод получает значение настройки.
     *
     * @return Значение настройки
     */
    public Object getValue() {
        return value;
    }

    /**
     * Метод задает новое значение настройки.
     *
     * @param value Новое значение настройки
     * @return
     */
    public SettingsEntity setValue(Object value) {
        this.value = value;
        return this;
    }
}
