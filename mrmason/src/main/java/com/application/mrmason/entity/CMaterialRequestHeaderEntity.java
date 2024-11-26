package com.application.mrmason.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "c_material_request_header")
public class CMaterialRequestHeaderEntity {

    @Id
    @Column(name = "c_mat_request_id", length = 20)
    private String materialRequestId;

    @Column(name = "total_qty")
    private int totalQty;

    @Column(name = "c_email", length = 45)
    private String customerEmail;

    @Column(name = "c_name", length = 45)
    private String customerName;

    @Column(name = "date")
    private LocalDate createdDate;

    @Column(name = "update_by", length = 45)
    private String updatedBy;

    @Column(name = "updated_date", length = 45)
    private LocalDate updatedDate;

    @Column(name = "quote_id", length = 45)
    private String quoteId;

    @Column(name = "requested_by", length = 45)
    private String requestedBy;

    @Column(name = "c_moble", length = 45)
    private String customerMobile;

    @Column(name = "delivery_date")
    private LocalDate deliveryDate;

    @Column(name = "delvery_location", length = 45)
    private String deliveryLocation;

    @PrePersist
    public void prePersist() {
        this.createdDate = LocalDate.now();
        this.updatedDate = LocalDate.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedDate = LocalDate.now();
    }
}
