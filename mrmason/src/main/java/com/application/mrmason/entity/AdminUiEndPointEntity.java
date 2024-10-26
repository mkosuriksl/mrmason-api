package com.application.mrmason.entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "admin_ui_end_point")
public class AdminUiEndPointEntity {

    @EmbeddedId
    private AdminUiEndPointId id;

    @Column(name = "updated_by", length = 45)
    private String updatedBy;

    @Column(name = "updated_time")
    private String updatedTime;

    @PrePersist
    @PreUpdate
    private void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a");
        this.updatedTime = now.format(formatter);
    }

    public AdminUiEndPointEntity() {
    }

    public AdminUiEndPointEntity(String systemId, String ipUrlToUi, String updatedBy) {
        this.id = new AdminUiEndPointId(systemId, ipUrlToUi);
        this.updatedBy = updatedBy;
    }
}
