package com.application.mrmason.dto;

import java.util.List;

import com.application.mrmason.entity.ServiceRequestElectricalQuotation;

import lombok.Data;

@Data
public class ResponseGetServiceRequestElectricalQuotationDto {
	private String message;
	private boolean status;
	private List<ServiceRequestElectricalQuotation> serviceRequestElectricalQuotation;
	private int currentPage;
	private int pageSize;
	private long totalElements;
	private int totalPages;
}