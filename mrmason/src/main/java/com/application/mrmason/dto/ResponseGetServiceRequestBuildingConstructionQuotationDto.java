package com.application.mrmason.dto;

import java.util.List;

import com.application.mrmason.entity.ServiceRequestBuildingConstructionQuotation;

import lombok.Data;

@Data
public class ResponseGetServiceRequestBuildingConstructionQuotationDto {
	private String message;
	private boolean status;
	private List<ServiceRequestBuildingConstructionQuotation> serviceRequestBuildingConstructionQuotation;
	private int currentPage;
	private int pageSize;
	private long totalElements;
	private int totalPages;
}