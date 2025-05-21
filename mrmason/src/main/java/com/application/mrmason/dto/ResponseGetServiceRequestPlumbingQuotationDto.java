package com.application.mrmason.dto;

import java.util.List;

import com.application.mrmason.entity.ServiceRequestPlumbingQuotation;

import lombok.Data;

@Data
public class ResponseGetServiceRequestPlumbingQuotationDto {
	private String message;
	private boolean status;
	private List<ServiceRequestPlumbingQuotation> requestPlumbingQuotations;
	private int currentPage;
	private int pageSize;
	private long totalElements;
	private int totalPages;
}