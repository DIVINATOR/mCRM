package io.github.divinator.datasource.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

/**
 * Сущность описывает запись для журнала звонков в таблице базы данных.
 */
@Table("t_call_history")
public class CallHistoryEntity {
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

    public CallHistoryEntity() {
    }

    public CallHistoryEntity(LocalDateTime createDateTime, boolean manually) {
        this.createDateTime = createDateTime;
        this.manually = manually;
    }

    public CallHistoryEntity(String phone, LocalDateTime createDateTime, LocalDateTime dateTime, boolean manually) {
        this(phone, createDateTime, dateTime, manually, 0);
    }

    public CallHistoryEntity(String phone, LocalDateTime createDateTime, LocalDateTime dateTime, boolean manually, long subtypeId) {
        this.phone = phone;
        this.createDateTime = createDateTime;
        this.dateTime = dateTime;
        this.manually = manually;
        this.subtypeId = subtypeId;
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
        return createDateTime;
    }

    public void setCreateDateTime(LocalDateTime createDateTime) {
        this.createDateTime = createDateTime;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
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