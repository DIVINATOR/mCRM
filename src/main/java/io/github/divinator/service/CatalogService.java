package io.github.divinator.service;

import io.github.divinator.datasource.entity.CatalogDetails;
import io.github.divinator.datasource.entity.CatalogSubtype;
import io.github.divinator.datasource.repository.CatalogDetailsRepository;
import io.github.divinator.datasource.repository.CatalogRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;
import java.util.ResourceBundle;

@Service
public class CatalogService {
    private final CatalogRepository catalogRepository;
    private final CatalogDetailsRepository catalogDetailsRepository;
    private final ResourceBundle resourceBundle;

    public CatalogService(CatalogRepository catalogRepository, CatalogDetailsRepository catalogDetailsRepository, ResourceBundle resourceBundle) {
        this.catalogRepository = catalogRepository;
        this.catalogDetailsRepository = catalogDetailsRepository;
        this.resourceBundle = resourceBundle;
        this.initCatalog();
    }

    public void initCatalog() {
        if (this.catalogRepository.count() == 0L) {
            this.catalogRepository.saveAll(
                    Arrays.asList(
                            CatalogSubtypeUtils.newInstance(this.resourceBundle, "catalog.subtype.0"),
                            CatalogSubtypeUtils.newInstance(this.resourceBundle, "catalog.subtype.1"),
                            CatalogSubtypeUtils.newInstance(this.resourceBundle, "catalog.subtype.2"),
                            CatalogSubtypeUtils.newInstance(this.resourceBundle, "catalog.subtype.3"),
                            CatalogSubtypeUtils.newInstance(this.resourceBundle, "catalog.subtype.4"),
                            CatalogSubtypeUtils.newInstance(this.resourceBundle, "catalog.subtype.5"),
                            CatalogSubtypeUtils.newInstance(this.resourceBundle, "catalog.subtype.6"),
                            CatalogSubtypeUtils.newInstance(this.resourceBundle, "catalog.subtype.7"),
                            CatalogSubtypeUtils.newInstance(this.resourceBundle, "catalog.subtype.8"),
                            CatalogSubtypeUtils.newInstance(this.resourceBundle, "catalog.subtype.9"),
                            CatalogSubtypeUtils.newInstance(this.resourceBundle, "catalog.subtype.10"),
                            CatalogSubtypeUtils.newInstance(this.resourceBundle, "catalog.subtype.11"),
                            CatalogSubtypeUtils.newInstance(this.resourceBundle, "catalog.subtype.12"),
                            CatalogSubtypeUtils.newInstance(this.resourceBundle, "catalog.subtype.13"),
                            CatalogSubtypeUtils.newInstance(this.resourceBundle, "catalog.subtype.14")
                    )
            );
        }

    }

    public Iterable<CatalogSubtype> getSubtypes() {
        return this.catalogRepository.findAll();
    }

    public CatalogSubtype getSubtypeByName(String name) {
        return this.catalogRepository.findAllByName(name);
    }

    public Optional<CatalogSubtype> getSubtypeById(long id) {
        return this.catalogRepository.findById(id);
    }

    public Optional<CatalogDetails> getCatalogDetailsById(long id) {
        return this.catalogDetailsRepository.findById(id);
    }
}
