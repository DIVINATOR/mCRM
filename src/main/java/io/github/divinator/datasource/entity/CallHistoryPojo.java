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

    public CallHistoryPojo(CallHistory callHistory, CatalogService catalogService) {
        this.id = callHistory.getId();
        this.date = callHistory.getDateTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy H:m:s"));
        this.phone = callHistory.getPhone();
        this.subtype = catalogService.getSubtypeById(callHistory.getSubtypeId()).get().getName();
        this.details = catalogService.getCatalogDetailsById(callHistory.getDetailsId()).orElse(new CatalogDetails(null)).getName();
        this.tid = callHistory.getTid();
        this.title = callHistory.getTitle();
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
