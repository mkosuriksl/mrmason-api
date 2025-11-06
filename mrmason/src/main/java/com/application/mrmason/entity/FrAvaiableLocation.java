package com.application.mrmason.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "fr_available_location")
public class FrAvaiableLocation {

    @Id
    @Column(name = "frUserId")
    private String frUserId;

    @Column(name = "city")
    private String city;
    
    @Column(name = "countrycode")
    private String countrycode;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @PrePersist
    @PreUpdate
    private void setDefaults() {
        this.updatedDate = LocalDateTime.now();
    }
}

