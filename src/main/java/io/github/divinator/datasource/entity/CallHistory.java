package io.github.divinator.datasource.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

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

    public CallHistory() {
        this.createDateTime = LocalDateTime.now();
    }

    public CallHistory(String phone, LocalDateTime dateTime, boolean manually) {
        this.phone = phone;
        this.createDateTime = LocalDateTime.now();
        this.dateTime = dateTime;
        this.manually = manually;
    }

    public CallHistory(String phone, LocalDateTime dateTime, boolean manually, long subtypeId) {
        this.phone = phone;
        this.createDateTime = LocalDateTime.now();
        this.dateTime = dateTime;
        this.manually = manually;
        this.subtypeId = subtypeId;
    }

    public CallHistory(String phone, LocalDateTime dateTime, boolean manually, long subtypeId, long detailsId, String tid, String title) {
        this.phone = phone;
        this.createDateTime = LocalDateTime.now();
        this.subtypeId = subtypeId;
        this.dateTime = dateTime;
        this.manually = manually;
        this.detailsId = detailsId;
        this.tid = tid;
        this.title = title;
    }

    @PersistenceConstructor
    public CallHistory(long id, String phone, LocalDateTime createDateTime, LocalDateTime dateTime, long subtypeId, long detailsId, String tid, String title, boolean manually) {
        this.id = id;
        this.phone = phone;
        this.createDateTime = createDateTime;
        this.dateTime = dateTime;
        this.subtypeId = subtypeId;
        this.detailsId = detailsId;
        this.tid = tid;
        this.title = title;
        this.manually = manually;
    }

    public long getId() {
        return this.id;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDateTime getCreateDateTime() {
        return this.createDateTime;
    }

    public LocalDateTime getDateTime() {
        return this.dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public long getSubtypeId() {
        return this.subtypeId;
    }

    public void setSubtypeId(long subtypeId) {
        this.subtypeId = subtypeId;
    }

    public long getDetailsId() {
        return this.detailsId;
    }

    public void setDetailsId(long detailsId) {
        this.detailsId = detailsId;
    }

    public String getTid() {
        return this.tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isManually() {
        return this.manually;
    }

    public void setManually(boolean manually) {
        this.manually = manually;
    }

    public void setCreateDateTime(LocalDateTime createDateTime) {
        this.createDateTime = createDateTime;
    }
}