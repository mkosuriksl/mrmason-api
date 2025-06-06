package com.application.mrmason.dto;

import java.util.List;

import com.application.mrmason.entity.ServiceRequestPaintOnlyQuotation;

import lombok.Data;

@Data
public class ResponseGetServiceRequestPaintOnlyQuotationDto {
	private String message;
	private boolean status;
	private List<ServiceRequestPaintOnlyQuotation> requestPaintOnlyQuotations;
	private int currentPage;
	private int pageSize;
	private long totalElements;
	private int totalPages;
}