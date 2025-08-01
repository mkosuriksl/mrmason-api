package com.application.mrmason.dto;

import java.util.List;

import com.application.mrmason.entity.SPAvailability;

import lombok.Data;

@Data
public class ResponseSPAvailabilityDto {
	private String message;
	private boolean status;
	private List<SPAvailability> getData;
	private int currentPage;
	private int pageSize;
	private long totalElements;
	private int totalPages;

}
