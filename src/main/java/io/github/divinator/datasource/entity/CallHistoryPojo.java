package io.github.divinator.datasource.entity;

import io.github.divinator.service.CatalogService;

import java.time.format.DateTimeFormatter;

public class CallHistoryPojo {

    private final long id;
    private final String dateTime;
    private final String phone;
    private final String type;
    private final String subtype;
    private final String details;
    private final String tid;
    private final String title;

    public CallHistoryPojo(CallHistoryEntity callHistoryEntity, CatalogService catalogService) {
        this.id = callHistoryEntity.getId();

        this.dateTime = callHistoryEntity.getDateTime().format(DateTimeFormatter.ofPattern("HH:mm"));

        this.phone = callHistoryEntity.getPhone();
        this.type = catalogService.getCatalogType(callHistoryEntity.getTypeId()).get().getName();

        this.subtype = catalogService.getCatalogSubtype(
                callHistoryEntity.getTypeId(),
                callHistoryEntity.getSubtypeId()
        ).get().getName();

        this.details = catalogService.getCatalogDetails(
                callHistoryEntity.getTypeId(),
                callHistoryEntity.getSubtypeId(),
                callHistoryEntity.getDetailsId()
        ).orElse(new CatalogDetails("")).getName();

        this.tid = callHistoryEntity.getTid();
        this.title = callHistoryEntity.getTitle();
    }

    public String getDateTime() {
        return dateTime;
    }

    public String getPhone() {
        return phone;
    }

    public String getType() {
        return type;
    }

    public String getSubtype() {
        return subtype;
    }

    public String getDetails() {
        return details;
    }

    public String getTid() {
        return tid;
    }

    public String getTitle() {
        return title;
    }
}
