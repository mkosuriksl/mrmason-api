package com.application.mrmason.dto;

import java.util.List;

import com.application.mrmason.entity.ServiceRequestBuildingConstructionQuotation;

import lombok.Data;

@Data
public class ServiceRequestBuildingConstructionQuotationWrapper {
	private String requestId;
    private List<ServiceRequestBuildingConstructionQuotation> items;
}
