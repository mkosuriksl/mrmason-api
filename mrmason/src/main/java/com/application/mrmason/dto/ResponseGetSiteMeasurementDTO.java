package com.application.mrmason.dto;

import java.util.List;

import lombok.Data;

@Data
public class ResponseGetSiteMeasurementDTO {

	private String message;
	private boolean status;
	private List<SiteMeasurementWithCustomerDTO> data;
	private int currentPage;
	private int pageSize;
	private long totalElements;
	private int totalPages;
}
