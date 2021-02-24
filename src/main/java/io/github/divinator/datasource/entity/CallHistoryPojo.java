package io.github.divinator.datasource.entity;

import io.github.divinator.service.CatalogService;
import java.time.format.DateTimeFormatter;

public class CallHistoryPojo {
    private final long id;
    private final String date;
    private final String phone;
    private final String subtype;
    private final String details;
    private final String tid;
    private final String title;

    public CallHistoryPojo(CallHistoryEntity callHistoryEntity, CatalogService catalogService) {
        this.id = callHistoryEntity.getId();
        this.date = callHistoryEntity.getDateTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy H:m:s"));
        this.phone = callHistoryEntity.getPhone();
        this.subtype = catalogService.getSubtypeById(callHistoryEntity.getSubtypeId()).get().getName();
        this.details = catalogService.getCatalogDetailsById(callHistoryEntity.getDetailsId()).orElse(new CatalogDetails(null)).getName();
        this.tid = callHistoryEntity.getTid();
        this.title = callHistoryEntity.getTitle();
    }

    public long getId() {
        return this.id;
    }

    public String getDate() {
        return this.date;
    }

    public String getPhone() {
        return this.phone;
    }

    public String getSubtype() {
        return this.subtype;
    }

    public String getDetails() {
        return this.details;
    }

    public String getTid() {
        return this.tid;
    }

    public String getTitle() {
        return this.title;
    }
}
