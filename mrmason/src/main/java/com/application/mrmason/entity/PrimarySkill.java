package com.application.mrmason.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "fr_primary_skill")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrimarySkill {

    @Id
    private String primarySkillId;   // FRxxxx_1001, FRxxxx_1002

    private String frUserId;

    private String primaryCoursename;

    private String primaryYearsOfExperience;

    private String primaryRating;
}

