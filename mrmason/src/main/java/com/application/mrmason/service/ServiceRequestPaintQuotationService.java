package com.application.mrmason.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.application.mrmason.dto.ServiceRequestItem;
import com.application.mrmason.entity.ServiceRequestPaintQuotation;
import com.application.mrmason.enums.RegSource;

public interface ServiceRequestPaintQuotationService {

	public List<ServiceRequestPaintQuotation> createServiceRequestPaintQuotationService(String requestId,
			String serviceCategory, List<ServiceRequestItem> items, RegSource regSource);

	public Page<ServiceRequestPaintQuotation> getServiceRequestPaintQuotationService(String admintasklineId,
			String requestLineIdDescription, String taskId, String serviceCategory, String measureNames, String status,
			String spId, Pageable pageable);

	public List<ServiceRequestPaintQuotation> updateServiceRequestQuotation(String requestId,
			List<ServiceRequestPaintQuotation> dtoList, RegSource regSource);

	public Map<String, Object> getAllGroupedQuotations(String admintasklineId, String taskDescription,
			String serviceCategory, String taskId, String measureNames, String status, String spId,String requestId, int page, int size);
}
