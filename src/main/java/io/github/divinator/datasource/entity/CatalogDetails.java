package io.github.divinator.datasource.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("t_catalog_details")
public class CatalogDetails implements Comparable<CatalogDetails> {
    @Id
    private long id;

    @Column("name")
    private final String name;

    @Column("CATALOG_SUBTYPE")
    private CatalogSubtype catalogSubtype;

    public CatalogDetails(String name) {
        this.name = name;
    }

    @PersistenceConstructor
    public CatalogDetails(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public CatalogSubtype getCatalogSubtypeId() {
        return this.catalogSubtype;
    }

    public int compareTo(CatalogDetails o) {
        return this.getName().compareTo(o.getName());
    }
}
