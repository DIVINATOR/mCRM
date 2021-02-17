package io.github.divinator.datasource.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("t_settings")
public class Settings {

    @Id
    private long id;

    @Column("key")
    private String key;

    @Column("value")
    private Object value;

    public Settings() {

    }

    @PersistenceConstructor
    public Settings(long id, String key, String value) {
        this.id = id;
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }
}
