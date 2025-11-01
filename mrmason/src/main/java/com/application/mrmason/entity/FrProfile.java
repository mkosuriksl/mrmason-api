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
@Table(name = "fr_profile")
public class FrProfile {

    @Id
    @Column(name = "frUserId")
    private String frUserId;

    @Convert(converter = StringListConverter.class)
    @Column(name = "primary_skill", length = 500)
    private List<String> primarySkill;

    @Column(name = "primary_yoe")
    private String primaryYoe;

    @Column(name = "primary_rate_my_self")
    private String primaryRateMySelf;

    @Convert(converter = StringListConverter.class)
    @Column(name = "secondary_skill", length = 500)
    private List<String> secondarySkill;

    @Column(name = "secondary_yoe")
    private String secondaryYoe;

    @Column(name = "secondary_rate_my_self")
    private String secondaryRateMySelf;

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

