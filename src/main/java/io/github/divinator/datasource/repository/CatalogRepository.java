package io.github.divinator.datasource.repository;

import io.github.divinator.datasource.entity.CatalogType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CatalogRepository extends CrudRepository<CatalogType, Long> {
    CatalogType findAllByName(String name);
}
