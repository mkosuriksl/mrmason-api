package com.application.mrmason.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.application.mrmason.entity.ServiceRequestElectricalQuotation;
import com.application.mrmason.enums.RegSource;


public interface ServiceRequestElectricalQuotationService {
	public List<ServiceRequestElectricalQuotation> createServiceRequestElectricalQuotationService(List<ServiceRequestElectricalQuotation> dtoList, RegSource regSource);
	public Page<ServiceRequestElectricalQuotation> getServiceRequestElectricalQuotationService(
			String serviceRequestElectricalId, 
			String requestLineId, String requestLineIdDescription, String requestId,Integer qty,
			Integer amount,String status,String spId,Pageable pageable);
}
