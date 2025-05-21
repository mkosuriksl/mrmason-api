package com.application.mrmason.dto;

import java.util.List;

import com.application.mrmason.entity.ServiceRequestElectricalQuotation;

import lombok.Data;

@Data
public class ServiceRequestElectricalQuotationWrapper {
	private String requestId;
    private List<ServiceRequestElectricalQuotation> items;
}
