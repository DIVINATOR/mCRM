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
    @Column("type_id")
    private long typeId;
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
    @Column("transferred")
    private boolean transferred;

    public CallHistoryEntity() {

    }

    public CallHistoryEntity(String phone, LocalDateTime createDateTime, LocalDateTime dateTime, long typeId, long subtypeId, long detailsId, String tid, String title, boolean manually, boolean transferred) {
        this.phone = phone;
        this.createDateTime = createDateTime;
        this.dateTime = dateTime;
        this.typeId = typeId;
        this.subtypeId = subtypeId;
        this.detailsId = detailsId;
        this.tid = tid;
        this.title = title;
        this.manually = manually;
        this.transferred = transferred;
    }

    public long getId() {
        return id;
    }

    public String getPhone() {
        return phone;
    }

    public LocalDateTime getCreateDateTime() {
        return createDateTime;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public long getTypeId() {
        return typeId;
    }

    public long getSubtypeId() {
        return subtypeId;
    }

    public long getDetailsId() {
        return detailsId;
    }

    public String getTid() {
        return tid;
    }

    public String getTitle() {
        return title;
    }

    public boolean isManually() {
        return manually;
    }

    public boolean isTransferred() {
        return transferred;
    }
}