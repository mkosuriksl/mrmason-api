package com.application.mrmason.entity;

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

@Getter
@Setter
@Entity
@Table(name = "user_membership_renewal_details")
public class SPMembRenewDetailsEntity {

    @Id
    @Column(name = "membership_order_id_line_item", nullable = false, length = 255)
    private String membershipOrderIdLineItem;

    @Column(name = "membership_order_id", nullable = false, length = 255)
    private String membershipOrderId;

    @Column(name = "store_id", nullable = false, length = 100)
    private String storeId;

    @Column(name = "order_amount", nullable = false)
    private int orderAmount;

    @Column(name = "order_date", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd, HH:mm:ss")
    private LocalDateTime orderDate;

    @Column(name = "order_placed_by", nullable = false, length = 100)
    private String orderPlacedBy;

    @Column(name = "plan_id", nullable = false, length = 50)
    private String planId;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @PrePersist
    public void onPrePersist() {
        this.orderDate = LocalDateTime.now();
    }

    @PreUpdate
    public void onPreUpdate() {
        this.orderDate = LocalDateTime.now();
    }

}
