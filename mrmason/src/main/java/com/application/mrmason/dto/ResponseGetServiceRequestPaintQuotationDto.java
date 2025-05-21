package com.application.mrmason.dto;

import java.util.List;

import com.application.mrmason.entity.ServiceRequestPaintQuotation;

import lombok.Data;

@Data
public class ResponseGetServiceRequestPaintQuotationDto {
	private String message;
	private boolean status;
	private List<ServiceRequestPaintQuotation> serviceRequestPaintQuotation;
	private int currentPage;
	private int pageSize;
	private long totalElements;
	private int totalPages;
}