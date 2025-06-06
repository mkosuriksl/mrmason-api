package com.application.mrmason.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.application.mrmason.entity.ServiceRequestPaintOnlyQuotation;
import com.application.mrmason.enums.RegSource;

public interface ServiceRequestPaintOnlyQuotationService {
	public List<ServiceRequestPaintOnlyQuotation> createServiceRequestPaintOnlyQuotation(
			String requestId,List<ServiceRequestPaintOnlyQuotation> dtoList, RegSource regSource);
	public Page<ServiceRequestPaintOnlyQuotation> getServiceRequestPaintOnlyQuotation(
			String requestLineId, String requestLineIdDescription, String requestId, Integer quotationAmount,
			String status, String spId, Pageable pageable);
	public List<ServiceRequestPaintOnlyQuotation> updateServiceRequestPaintOnlyQuotation(
	        String requestId, List<ServiceRequestPaintOnlyQuotation> dtoList, RegSource regSource);
}
