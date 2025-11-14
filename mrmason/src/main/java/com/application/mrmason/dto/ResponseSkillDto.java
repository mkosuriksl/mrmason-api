package com.application.mrmason.dto;

import java.util.List;

import lombok.Data;

@Data
public class ResponseSkillDto {
	private String message;
	private boolean status;

	private List<PrimarySkillResponseDto> primarySkills;
	private List<SecondarySkillResponseDto> secondarySkills;

	private int currentPage;
	private int pageSize;
	private long totalElements;
	private int totalPages;
}
