package com.application.mrmason.dto;

import java.util.List;

import com.application.mrmason.entity.ServiceRequestPaintQuotation;

import lombok.Data;

@Data
public class ServiceRequestPaintQuotationWrapper {
	private String requestId;
    private List<ServiceRequestPaintQuotation> items;
}
