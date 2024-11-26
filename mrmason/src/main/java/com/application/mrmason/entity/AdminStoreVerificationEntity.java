package com.application.mrmason.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "admin_store_verification")
public class AdminStoreVerificationEntity {

    @Id
    @Column(name = "sp_userid_store_id")
    private String bodSeqNoStoreId;

    @Column(name = "store_id")
    private String storeId;

    @Column(name = "sp_userid")
    private String bodSeqNo;

    @Column(name = "verification_status")
    private String verificationStatus;

    @Column(name = "verification_comment")
    private String verificationComment;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "updated_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd, HH:mm:ss", timezone = "Asia/Kolkata")
    private Timestamp updatedDate;

    @PrePersist
    @PreUpdate
    private void prePersistOrUpdate() {
        if (this.bodSeqNoStoreId == null && bodSeqNo != null && storeId != null) {
            this.bodSeqNoStoreId = bodSeqNo + "_" + storeId;
        }

        this.updatedDate = Timestamp.valueOf(LocalDateTime.now());
    }
}
