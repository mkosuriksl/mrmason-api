package com.application.mrmason.dto;

import java.util.List;

import com.application.mrmason.entity.FrAvailable;

import lombok.Data;

@Data
public class ResponseAvailableDto {
	private String message;
	private boolean status;

	private List<FrAvailable> available;

	private int currentPage;
	private int pageSize;
	private long totalElements;
	private int totalPages;
}
