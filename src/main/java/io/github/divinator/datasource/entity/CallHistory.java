package io.github.divinator.datasource.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Table("t_call_history")
public class CallHistory {
    @Id
    private long id;
    @Column("phone")
    private String phone;
    @Column("create_datetime")
    private LocalDateTime createDateTime;
    @Column("date")
    private LocalDateTime dateTime;
    @Column("subtype_id")
    private long subtypeId;
    @Column("details_id")
    private long detailsId;
    @Column("terminal_id")
    private String tid;
    @Column("title")
    private String title;
    @Column("manually")
    private boolean manually;

    @Transient
    private final ZoneId zone = ZoneId.of("Europe/Moscow");

    public CallHistory() {
        this(null, LocalDateTime.now(), false);
    }

    public CallHistory(String phone, LocalDateTime dateTime, boolean manually) {
        this(phone, dateTime, manually, 0);
    }

    public CallHistory(String phone, LocalDateTime dateTime, boolean manually, long subtypeId) {
        this.phone = phone;
        this.createDateTime = convertToMoscow(LocalDateTime.now());
        this.dateTime = convertToMoscow(dateTime);
        this.manually = manually;
        this.subtypeId = subtypeId;
    }

    private LocalDateTime convertToMoscow(LocalDateTime localDateTime) {
        return ZonedDateTime.of(localDateTime, ZonedDateTime.now().getZone())
                .withZoneSameInstant(zone)
                .toLocalDateTime();
    }

    private LocalDateTime convertFromMoscow(LocalDateTime localDateTime) {
        return ZonedDateTime.of(localDateTime, zone)
                .withZoneSameInstant(ZonedDateTime.now().getZone())
                .toLocalDateTime();
    }

    public long getId() {
        return id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDateTime getCreateDateTime() {
        return convertFromMoscow(createDateTime);
    }

    public void setCreateDateTime(LocalDateTime createDateTime) {
        this.createDateTime = convertToMoscow(createDateTime);
    }

    public LocalDateTime getDateTime() {
        return convertFromMoscow(dateTime);
    }

    public LocalDateTime getDateTimeFromDB() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = convertToMoscow(dateTime);
    }

    public long getSubtypeId() {
        return subtypeId;
    }

    public void setSubtypeId(long subtypeId) {
        this.subtypeId = subtypeId;
    }

    public long getDetailsId() {
        return detailsId;
    }

    public void setDetailsId(long detailsId) {
        this.detailsId = detailsId;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isManually() {
        return manually;
    }

    public void setManually(boolean manually) {
        this.manually = manually;
    }
}