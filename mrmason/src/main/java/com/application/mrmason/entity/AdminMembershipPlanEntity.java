package com.application.mrmason.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "admin_membership_plan")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminMembershipPlanEntity {
    @Id
    @Column(name = "membership_plan_id", length = 20)
    private String membershipPlanId;

    @Column(name = "amount")
    private int amount;

    @Column(name = "no_of_days_valid", length = 10)
    private String noOfDaysValid;

    @Column(name = "plan_name", length = 20)
    private String planName;

    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @PrePersist
    public void onPrePersist() {
        this.updatedDate = LocalDateTime.now();
    }

    @PreUpdate
    public void onPreUpdate() {
        this.updatedDate = LocalDateTime.now();
    }
}
