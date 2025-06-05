package com.application.mrmason.dto;

import java.util.List;

import lombok.Data;

@Data
public class ServiceRequestPaintQuotationWrapper {
	private String requestId;
	private String serviceCategory;
	private List<ServiceRequestItem> items;
}
