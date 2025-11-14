package com.application.mrmason.dto;

import java.util.List;

import com.application.mrmason.entity.PrimarySkill;
import com.application.mrmason.entity.SecondarySkill;

import lombok.Data;

@Data
public class SkillRequestDto {

	private String frUserId;

	private List<PrimarySkill> primarySkills;

	private List<SecondarySkill> secondarySkills;
}
