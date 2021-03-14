package io.github.divinator.service;

import io.github.divinator.datasource.entity.CatalogDetails;
import io.github.divinator.datasource.entity.CatalogSubtype;
import io.github.divinator.datasource.entity.CatalogType;
import io.github.divinator.datasource.repository.CatalogDetailsRepository;
import io.github.divinator.datasource.repository.CatalogRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Predicate;

@Service
public class CatalogService {
    private final CatalogRepository catalogRepository;
    private final CatalogDetailsRepository catalogDetailsRepository;
    private final ResourceBundle resourceBundle;

    public CatalogService(CatalogRepository catalogRepository, CatalogDetailsRepository catalogDetailsRepository, ResourceBundle resourceBundle) {
        this.catalogRepository = catalogRepository;
        this.catalogDetailsRepository = catalogDetailsRepository;
        this.resourceBundle = resourceBundle;
        //this.initCatalog();
    }

    public long count() {
        return catalogRepository.count();
    }

    public void saveCatalogType(CatalogType catalogType) {
        catalogRepository.save(catalogType);
    }

    public void saveCatalogTypes(Iterable<CatalogType> catalogTypes) {
        catalogRepository.saveAll(catalogTypes);
    }

    public CatalogType getCatalogType(String name) {
        return catalogRepository.findAllByName(name);
    }

    public Optional<CatalogType> getCatalogType(long typeId) {
        return catalogRepository.findById(typeId);
    }

    public Optional<CatalogSubtype> getCatalogSubtype(long typeId, long subtypeId) {
        return getCatalogType(typeId).get().getSubtypes().stream().filter(new Predicate<CatalogSubtype>() {
            @Override
            public boolean test(CatalogSubtype subtype) {
                return subtype.getSubtypeId() == subtypeId;
            }
        }).findFirst();
    }

    public Optional<CatalogDetails> getCatalogDetails(long typeId, long subtypeId, long detailsId) {
        return getCatalogSubtype(typeId, subtypeId).get().getDetails().stream().filter(new Predicate<CatalogDetails>() {
            @Override
            public boolean test(CatalogDetails details) {
                return details.getId() == detailsId;
            }
        }).findFirst();
    }

    public Iterable<CatalogType> getAllCatalogType() {
        return catalogRepository.findAll();
    }
}
