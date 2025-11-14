package com.application.mrmason.dto;

import java.util.List;

import com.application.mrmason.entity.FrAvaiableLocation;

import lombok.Data;

@Data
public class ResponseAvailableDto {
	private String message;
	private boolean status;

	private List<FrAvaiableLocation> avaiableLocations;

	private int currentPage;
	private int pageSize;
	private long totalElements;
	private int totalPages;
}
