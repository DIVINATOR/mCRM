package io.github.divinator.datasource.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.util.HashSet;
import java.util.Set;

@Table("t_catalog_type")
public class CatalogType implements Comparable<CatalogType>{

    @Id
    private long typeId;

    @Column("name")
    private String name;

    @MappedCollection(keyColumn = "TYPE_ID")
    private Set<CatalogSubtype> subtypes;

    public CatalogType() {
    }

    public CatalogType(String name) {
        this(name, new HashSet<>());
    }

    public CatalogType(String name, Set<CatalogSubtype> subtypes) {
        this.name = name;
        this.subtypes = subtypes;
    }

    public long getTypeId() {
        return typeId;
    }

    public String getName() {
        return name;
    }

    public Set<CatalogSubtype> getSubtypes() {
        return subtypes;
    }

    public void addCatalogSubtype(CatalogSubtype subtype) {
        this.subtypes.add(subtype);
    }

    @Override
    public int compareTo(CatalogType o) {
        return this.getName().compareTo(o.getName());
    }
}
