package com.application.mrmason.dto;

import java.util.List;

import com.application.mrmason.entity.ServiceRequestPaintOnlyQuotation;

import lombok.Data;

@Data
public class ServiceRequestPaintOnlyQuotationWrapper {
	private String requestId;
    private List<ServiceRequestPaintOnlyQuotation> items;
}
