package com.application.mrmason.dto;

import java.util.List;

import com.application.mrmason.entity.ServiceRequestPlumbingQuotation;

import lombok.Data;

@Data
public class ServiceRequestPlumbingQuotationWrapper {
	private String requestId;
    private List<ServiceRequestPlumbingQuotation> items;
}
