package com.application.mrmason.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.application.mrmason.entity.ServiceRequestPaintQuotation;
import com.application.mrmason.enums.RegSource;

@Service
public interface ServiceRequestPaintQuotationService {
	public List<ServiceRequestPaintQuotation> createServiceRequestPaintQuotationService(
		    String requestId, List<ServiceRequestPaintQuotation> dtoList, RegSource regSource);
	public Page<ServiceRequestPaintQuotation> getServiceRequestPaintQuotationService(
			 String requestLineId, String requestLineIdDescription, String requestId,
			Integer quotationAmount,String status,String spId,Pageable pageable);
}
