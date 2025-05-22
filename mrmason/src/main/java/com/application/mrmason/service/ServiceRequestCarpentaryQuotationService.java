package com.application.mrmason.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.application.mrmason.entity.ServiceRequestCarpentaryQuotation;
import com.application.mrmason.enums.RegSource;

@Service
public interface ServiceRequestCarpentaryQuotationService {
	public List<ServiceRequestCarpentaryQuotation> createServiceRequestCarpentaryQuotationService(String requestId,
			List<ServiceRequestCarpentaryQuotation> dtoList, RegSource regSource);

	public Page<ServiceRequestCarpentaryQuotation> getServiceRequestCarpentaryQuotationService(String requestLineId,
			String requestLineIdDescription, String requestId, Integer quotationAmount, String status, String spId,
			Pageable pageable);

	public List<ServiceRequestCarpentaryQuotation> updateServiceRequestCarpentaryQuotationService(String requestId,
			List<ServiceRequestCarpentaryQuotation> dtoList, RegSource regSource);
}
