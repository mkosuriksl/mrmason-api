package com.application.mrmason.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
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
@Table(name = "fr_service_role")
public class FrServiceRole {

    @Id
    @Column(name = "frUserId")
    private String frUserId;

    @Convert(converter = StringListConverter.class)
    @Column(name = "training", length = 500)
    private List<String> training;

    @Convert(converter = StringListConverter.class)
    @Column(name = "developer", length = 500)
    private List<String> developer;

    @Column(name = "interviewer")
    private String interviewer;

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

