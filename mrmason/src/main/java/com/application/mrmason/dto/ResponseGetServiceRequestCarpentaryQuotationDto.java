package com.application.mrmason.dto;

import java.util.List;

import com.application.mrmason.entity.ServiceRequestCarpentaryQuotation;

import lombok.Data;

@Data
public class ResponseGetServiceRequestCarpentaryQuotationDto {
	private String message;
	private boolean status;
	private List<ServiceRequestCarpentaryQuotation> serviceRequestCarpentaryQuotation;
	private int currentPage;
	private int pageSize;
	private long totalElements;
	private int totalPages;
}