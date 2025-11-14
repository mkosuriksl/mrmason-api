package com.application.mrmason.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "fr_secondary_skill")
@Data
public class SecondarySkill {

    @Id
    private String secondarySkillId;  // FRxxxx_2001, FRxxxx_2002

    private String frUserId;

    private String secondaryCoursename;

    private String secondaryYearsOfExperience;

    private String secondaryRating;
}


