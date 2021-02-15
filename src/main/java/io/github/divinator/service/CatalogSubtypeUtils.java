package io.github.divinator.service;

import io.github.divinator.datasource.entity.CatalogDetails;
import io.github.divinator.datasource.entity.CatalogSubtype;
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

public interface CatalogSubtypeUtils {
    static CatalogSubtype newInstance(ResourceBundle resourceBundle, String subtype) {
        return new CatalogSubtype(resourceBundle.getString(subtype), (Set)Collections.list(resourceBundle.getKeys()).stream().filter((s) -> {
            return s.contains(String.format("%s.details", subtype));
        }).map((s) -> {
            return new CatalogDetails(resourceBundle.getString(s));
        }).collect(Collectors.toSet()));
    }
}