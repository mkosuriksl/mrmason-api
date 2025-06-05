package com.application.mrmason.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.application.mrmason.entity.ServiceRequestPlumbingQuotation;
import com.application.mrmason.enums.RegSource;

public interface ServiceRequestPlumbingQuotationService {
	public List<ServiceRequestPlumbingQuotation> createServiceRequestPlumbingQuotation(
			String requestId,List<ServiceRequestPlumbingQuotation> dtoList, RegSource regSource);
	public Page<ServiceRequestPlumbingQuotation> getServiceRequestPlumbingQuotation(
			String requestLineId, String requestLineIdDescription, String requestId, Integer quotationAmount,
			String status, String spId, Pageable pageable);
	public List<ServiceRequestPlumbingQuotation> updateServiceRequestPlumbingQuotation(
	        String requestId, List<ServiceRequestPlumbingQuotation> dtoList, RegSource regSource);
}
