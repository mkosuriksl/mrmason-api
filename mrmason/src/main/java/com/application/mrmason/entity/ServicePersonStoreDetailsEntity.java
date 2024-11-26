package com.application.mrmason.entity;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_store_details")
@Getter
@Setter
public class ServicePersonStoreDetailsEntity {

    @Id
    @Column(name = "sp_userid_store_id", length = 50)
    private String bodSeqNoStoreId;

    @Column(name = "sp_userid", length = 50, nullable = false)
    private String bodSeqNo;

    @Column(name = "store_id", length = 50)
    private String storeId;

    @Column(name = "store_expiry_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate storeExpiryDate;

    @Column(name = "store_current_plan", length = 20)
    private String storeCurrentPlan;

    @Column(name = "verification_status", length = 50)
    private String verificationStatus;

    @Column(name = "location", length = 100)
    private String location;

    @Column(name = "gst", length = 15)
    private String gst;

    @Column(name = "gst_document", columnDefinition = "LONGTEXT")
    private String gstDocument;

    @Column(name = "trade_license", length = 50)
    private String tradeLicense;

    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    @Column(name = "updated_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd, HH:mm:ss")
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
