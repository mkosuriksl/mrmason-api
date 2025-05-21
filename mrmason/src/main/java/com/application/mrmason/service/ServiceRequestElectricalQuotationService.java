package com.application.mrmason.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.application.mrmason.entity.ServiceRequestElectricalQuotation;
import com.application.mrmason.enums.RegSource;


public interface ServiceRequestElectricalQuotationService {
	public List<ServiceRequestElectricalQuotation> createServiceRequestElectricalQuotationService(
	        String requestId, List<ServiceRequestElectricalQuotation> dtoList, RegSource regSource);
	public Page<ServiceRequestElectricalQuotation> getServiceRequestElectricalQuotationService(
			String requestLineId, String requestLineIdDescription, String requestId,Integer qty,
			Integer amount,String status,String spId,Pageable pageable);
	public List<ServiceRequestElectricalQuotation> updateServiceRequestElectricalQuotation(
	        String requestId, List<ServiceRequestElectricalQuotation> dtoList, RegSource regSource);
}
