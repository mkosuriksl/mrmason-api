package com.application.mrmason.dto;

import java.util.List;

import com.application.mrmason.entity.FrPositionType;

import lombok.Data;

@Data
public class ResponsePositionTypeDto {
	private String message;
	private boolean status;

	private List<FrPositionType> positionTypes;

	private int currentPage;
	private int pageSize;
	private long totalElements;
	private int totalPages;
}
