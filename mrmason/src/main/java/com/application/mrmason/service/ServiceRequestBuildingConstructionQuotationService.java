package com.application.mrmason.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.application.mrmason.entity.ServiceRequestBuildingConstructionQuotation;
import com.application.mrmason.enums.RegSource;

@Service
public interface ServiceRequestBuildingConstructionQuotationService {
	public List<ServiceRequestBuildingConstructionQuotation> createServiceRequestBuildingConstructionQuotation(
		    String requestId, List<ServiceRequestBuildingConstructionQuotation> dtoList, RegSource regSource);
	public Page<ServiceRequestBuildingConstructionQuotation> getServiceRequestBuildingConstructionQuotation(
			 String requestLineId, String requestLineIdDescription, String requestId,
			Integer quotationAmount,String status,String spId,Pageable pageable);
	public List<ServiceRequestBuildingConstructionQuotation> updateServiceRequestBuildingConstructionQuotation(
	        String requestId, List<ServiceRequestBuildingConstructionQuotation> dtoList, RegSource regSource);
}
