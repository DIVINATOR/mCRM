package io.github.divinator.config;

import io.github.divinator.datasource.entity.CatalogDetails;
import io.github.divinator.datasource.entity.CatalogSubtype;
import io.github.divinator.datasource.entity.CatalogType;
import io.github.divinator.service.CatalogService;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.HashSet;
import java.util.TreeSet;

@Configuration
public class CatalogConfig {

    private final CatalogService catalogService;

    public CatalogConfig(CatalogService catalogService) {
        this.catalogService = catalogService;
        initializeCatalog();
    }

    private void initializeCatalog() {
        if (catalogService.count() == 0) {

            final TreeSet<CatalogDetails> details2in1 = new TreeSet<>(Arrays.asList(
                    new CatalogDetails("Обновление приложения"),
                    new CatalogDetails("ГЗ – не выполнена/срыв звонка"),
                    new CatalogDetails("ГЗ – другое"),
                    new CatalogDetails("ГЗ – касса зависла"),
                    new CatalogDetails("ГЗ – касса не видит ФН"),
                    new CatalogDetails("ГЗ – не включается"),
                    new CatalogDetails("ГЗ – нет связи"),
                    new CatalogDetails("ГЗ – проблема с печатью"),
                    new CatalogDetails("ГЗ – проблема с форматом S\\N"),
                    new CatalogDetails("ГЗ – физическое повреждение"),
                    new CatalogDetails("Прочее")
            ));

            // TMS
            CatalogType type0 = new CatalogType("TMS", new TreeSet<>(Arrays.asList(
                    new CatalogSubtype("Акси", details2in1),
                    new CatalogSubtype("Салют", details2in1)
            )));

            // Автономный_терминал
            CatalogType type1 = new CatalogType("Автономный_терминал", new TreeSet<>(Arrays.asList(
                    new CatalogSubtype("АВ\\Установка", new TreeSet<>(Arrays.asList(
                            new CatalogDetails("РКП\\ФКП"),
                            new CatalogDetails("Нет доступа в МСОМ"),
                            new CatalogDetails("Ошибка в МСОМ"),
                            new CatalogDetails("Нет функционала в МСОМ"),
                            new CatalogDetails("Отказ"),
                            new CatalogDetails("UPOS 2")
                    ))),
                    new CatalogSubtype("АВ\\Замена_оборудования", new TreeSet<>(Arrays.asList(
                            new CatalogDetails("РКП\\ФКП"),
                            new CatalogDetails("Нет доступа в МСОМ"),
                            new CatalogDetails("Ошибка в МСОМ"),
                            new CatalogDetails("Нет функционала в МСОМ"),
                            new CatalogDetails("Отказ"),
                            new CatalogDetails("UPOS 2"),
                            new CatalogDetails("Смена решения")
                    ))),
                    new CatalogSubtype("АВ\\Изменение_настроек", new TreeSet<>(Arrays.asList(
                            new CatalogDetails("РКП\\ФКП"),
                            new CatalogDetails("Нет доступа в МСОМ"),
                            new CatalogDetails("Ошибка в МСОМ"),
                            new CatalogDetails("Нет функционала в МСОМ"),
                            new CatalogDetails("Отказ"),
                            new CatalogDetails("UPOS 2")
                    ))),
                    new CatalogSubtype("АВ\\Изменение_версии", new HashSet<>(Arrays.asList(
                            new CatalogDetails("Спецпризнак"),
                            new CatalogDetails("Файлсет"),
                            new CatalogDetails("Инженер не знает об автоназначении"),
                            new CatalogDetails("Тираж"),
                            new CatalogDetails("UPOS 2")
                    ))),
                    new CatalogSubtype("АВ\\Консультация", new HashSet<>(Arrays.asList(
                            new CatalogDetails("По операциям из SV"),
                            new CatalogDetails("Поиск по серийнику"),
                            new CatalogDetails("Ошибка UPOS"),
                            new CatalogDetails("Проблема при УЗ"),
                            new CatalogDetails("Некорректная работа терминала"),
                            new CatalogDetails("По текущим настройкам"),
                            new CatalogDetails("Код активации"),
                            new CatalogDetails("Новый функционал"),
                            new CatalogDetails("UPOS 2"),
                            new CatalogDetails("Отказ")
                    ))),
                    new CatalogSubtype("АВ\\Мультимерчант", new TreeSet<>(Arrays.asList(
                            new CatalogDetails("Добавление отделов"),
                            new CatalogDetails("Изменение отделов"),
                            new CatalogDetails("Удаление отделов")
                    )))
            )));

            // Вендинг
            CatalogType type2 = new CatalogType("Вендинг", new TreeSet<>(Arrays.asList(
                    new CatalogSubtype("Ве\\Установка"),
                    new CatalogSubtype("Замена"),
                    new CatalogSubtype("Ве\\Изменение_настроек"),
                    new CatalogSubtype("Ве\\Изменение_версии"),
                    new CatalogSubtype("Ве\\Консультация")
            )));

            // ИКР
            CatalogType type3 = new CatalogType("ИКР", new TreeSet<>(Arrays.asList(
                    new CatalogSubtype("ИК\\Установка", new TreeSet<>(Arrays.asList(
                            new CatalogDetails("РКП\\ФКП настроен"),
                            new CatalogDetails("ФКП переведен"),
                            new CatalogDetails("Нет доступа в МСОМ"),
                            new CatalogDetails("Ошибка в МСОМ"),
                            new CatalogDetails("Нет функционала в МСОМ"),
                            new CatalogDetails("Отказ"),
                            new CatalogDetails("UPOS 2")
                    ))),
                    new CatalogSubtype("ИК\\Замена_оборудования", new TreeSet<>(Arrays.asList(
                            new CatalogDetails("РКП\\ФКП настроен"),
                            new CatalogDetails("ФКП переведен"),
                            new CatalogDetails("Смена решения"),
                            new CatalogDetails("Нет доступа в МСОМ"),
                            new CatalogDetails("Ошибка в МСОМ"),
                            new CatalogDetails("Нет функционала в МСОМ"),
                            new CatalogDetails("Отказ"),
                            new CatalogDetails("UPOS 2"),
                            new CatalogDetails("Смена решения (на ИКР)")
                    ))),
                    new CatalogSubtype("ИК\\Изменение_настроек", new TreeSet<>(Arrays.asList(
                            new CatalogDetails("РКП\\ФКП настроен"),
                            new CatalogDetails("ФКП переведен"),
                            new CatalogDetails("Смена решения"),
                            new CatalogDetails("Нет доступа в МСОМ"),
                            new CatalogDetails("Ошибка в МСОМ"),
                            new CatalogDetails("Нет функционала в МСОМ"),
                            new CatalogDetails("Отказ"),
                            new CatalogDetails("Настройка связи"),
                            new CatalogDetails("UPOS 2")
                    ))),
                    new CatalogSubtype("ИК\\Изменение_версии", new TreeSet<>(Arrays.asList(
                            new CatalogDetails("Паспорт"),
                            new CatalogDetails("Спецпризнак"),
                            new CatalogDetails("Файлсет"),
                            new CatalogDetails("Инженер не знает об автоназначении"),
                            new CatalogDetails("Тираж"),
                            new CatalogDetails("Ограничение кассового ПО"),
                            new CatalogDetails("Агент"),
                            new CatalogDetails("UPOS 2")
                    ))),
                    new CatalogSubtype("ИК\\Консультация", new TreeSet<>(Arrays.asList(
                            new CatalogDetails("По операциям из SV"),
                            new CatalogDetails("Поиск по серийнику"),
                            new CatalogDetails("Ошибка UPOS"),
                            new CatalogDetails("Проблема при УЗ"),
                            new CatalogDetails("Некорректная работа терминала"),
                            new CatalogDetails("По текущим настройкам"),
                            new CatalogDetails("Код активации"),
                            new CatalogDetails("По паспорту"),
                            new CatalogDetails("Новый функционал"),
                            new CatalogDetails("UPOS 2"),
                            new CatalogDetails("Отказ")
                    ))),
                    new CatalogSubtype("ИК\\Мультимерчант", new TreeSet<>(Arrays.asList(
                            new CatalogDetails("Добавление отделов"),
                            new CatalogDetails("Изменение отделов"),
                            new CatalogDetails("Удаление отделов")
                    )))
            )));

            // Прочее
            CatalogType type4 = new CatalogType("Прочее", new TreeSet<>(Arrays.asList(
                    new CatalogSubtype("Срыв_звонка")
            )));

            // Смарт_касса
            CatalogType type5 = new CatalogType("Смарт_касса", new TreeSet<>(Arrays.asList(
                    new CatalogSubtype("См\\Установка", new TreeSet<>(Arrays.asList(
                            new CatalogDetails("Эвотор +"),
                            new CatalogDetails("Cashout"),
                            new CatalogDetails("Мои платежи"),
                            new CatalogDetails("2в1 UPOS1"),
                            new CatalogDetails("2в1 UPOS 2"),
                            new CatalogDetails("Отказ")
                    ))),
                    new CatalogSubtype("См\\Замена_оборудования", new TreeSet<>(Arrays.asList(
                            new CatalogDetails("Эвотор +"),
                            new CatalogDetails("2в1 UPOS1"),
                            new CatalogDetails("2в1 UPOS 2"),
                            new CatalogDetails("Отказ")
                    ))),
                    new CatalogSubtype("См\\Изменение_настроек"),
                    new CatalogSubtype("См\\Изменение_версии", new TreeSet<>(Arrays.asList(
                            new CatalogDetails("Cashout"),
                            new CatalogDetails("Мои платежи"),
                            new CatalogDetails("2в1 UPOS1"),
                            new CatalogDetails("2в1 UPOS 2"),
                            new CatalogDetails("Инженер не знает об автоназначении")
                    ))),
                    new CatalogSubtype("См\\Консультация", new TreeSet<>(Arrays.asList(
                            new CatalogDetails("По операциям из SV"),
                            new CatalogDetails("Поиск по серийнику"),
                            new CatalogDetails("Ошибка UPOS"),
                            new CatalogDetails("Некорректная работа устройства"),
                            new CatalogDetails("По текущим настройкам"),
                            new CatalogDetails("Код активации"),
                            new CatalogDetails("Новый функционал")
                    )))
            )));

            // Транспорт
            CatalogType type6 = new CatalogType("Транспорт", new TreeSet<>(Arrays.asList(
                    new CatalogSubtype("Тр\\Установка"),
                    new CatalogSubtype("Тр\\Замена_оборудования"),
                    new CatalogSubtype("Тр\\Изменение_настроек"),
                    new CatalogSubtype("Тр\\Изменение_версии"),
                    new CatalogSubtype("Тр\\Консультация")
            )));

            catalogService.saveCatalogTypes(Arrays.asList(type0, type1, type2, type3, type4, type5, type6));
        }
    }
}
