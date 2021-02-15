package io.github.divinator.datasource.repository;

import io.github.divinator.datasource.entity.CatalogSubtype;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CatalogRepository extends CrudRepository<CatalogSubtype, Long> {
    CatalogSubtype findAllByName(String name);
}
