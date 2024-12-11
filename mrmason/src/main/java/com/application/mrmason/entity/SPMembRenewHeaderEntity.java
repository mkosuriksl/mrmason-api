package com.application.mrmason.entity;


import java.time.LocalDateTime;

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
@Table(name = "user_membership_renewal_header")
public class SPMembRenewHeaderEntity {

    @Id
    @Column(name = "membership_order_id")
    private String membershipOrderId;

    @Column(name = "order_amount")
    private int orderAmount;

    @Column(name = "order_date")
    private LocalDateTime orderDate;

    @Column(name = "order_placed_by")
    private String orderPlacedBy;

    @Column(name = "status")
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
