package io.github.divinator.datasource.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.util.HashSet;
import java.util.Set;

@Table("t_catalog_subtype")
public class CatalogSubtype implements Comparable<CatalogSubtype> {
    @Id
    private long subtypeId;
    @Column("name")
    private final String name;
    @MappedCollection(
            keyColumn = "SUBTYPE_ID"
    )
    private final Set<CatalogDetails> details;

    public CatalogSubtype(String name) {
        this(name, new HashSet());
    }

    public CatalogSubtype(String name, Set<CatalogDetails> details) {
        this.name = name;
        this.details = details;
    }

    @PersistenceConstructor
    public CatalogSubtype(long subtypeId, String name, Set<CatalogDetails> details) {
        this.subtypeId = subtypeId;
        this.name = name;
        this.details = details;
    }

    public long getSubtypeId() {
        return this.subtypeId;
    }

    public String getName() {
        return this.name;
    }

    public Set<CatalogDetails> getDetails() {
        return this.details;
    }

    public void addCatalogDetails(CatalogDetails detail) {
        this.details.add(detail);
    }

    public int compareTo(CatalogSubtype o) {
        return this.getName().compareTo(o.getName());
    }
}
