package com.application.mrmason.dto;

import java.util.List;

import com.application.mrmason.entity.ServiceRequestHeaderAllQuotation;
import com.application.mrmason.entity.ServiceRequestHeaderAllQuotationHistory;

import lombok.Data;

@Data
public class ResponseGetServiceRequestHeaderQuotationDto {
	private String message;
	private boolean status;
	private List<ServiceRequestHeaderAllQuotation> serviceRequestHeaderQuotation;
	private List<ServiceRequestHeaderAllQuotationHistory>requestHeaderAllQuotationHistories;
	private int currentPage;
	private int pageSize;
	private long totalElements;
	private int totalPages;
}