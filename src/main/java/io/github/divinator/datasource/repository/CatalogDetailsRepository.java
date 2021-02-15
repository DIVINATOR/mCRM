package io.github.divinator.datasource.repository;

import io.github.divinator.datasource.entity.CatalogDetails;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CatalogDetailsRepository extends PagingAndSortingRepository<CatalogDetails, Long> {
}
