package com.application.mrmason.dto;

import java.util.List;

import com.application.mrmason.entity.ServiceRequestCarpentaryQuotation;

import lombok.Data;

@Data
public class ServiceRequestCarpentaryQuotationWrapper {
	private String requestId;
    private List<ServiceRequestCarpentaryQuotation> items;
}
