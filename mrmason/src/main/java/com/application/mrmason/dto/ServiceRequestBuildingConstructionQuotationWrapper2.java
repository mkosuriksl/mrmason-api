package com.application.mrmason.dto;

import java.util.List;

import com.application.mrmason.entity.ServiceRequestBuildingConstructionQuotation;

import lombok.Data;

@Data
public class ServiceRequestBuildingConstructionQuotationWrapper2 {
	private String quotationId;
    private List<ServiceRequestBuildingConstructionQuotation> items;
}
