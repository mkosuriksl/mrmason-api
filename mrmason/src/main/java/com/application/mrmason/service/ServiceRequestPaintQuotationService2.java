package com.application.mrmason.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.application.mrmason.dto.GenericResponse;
import com.application.mrmason.dto.ResponseGetWorkOrderSRHdrAndCustomerDto;
import com.application.mrmason.dto.WorkOrderRequest;
import com.application.mrmason.entity.ServiceRequestHeaderAllQuotation2;
import com.application.mrmason.entity.ServiceRequestPaintQuotation2;
import com.application.mrmason.enums.RegSource;

public interface ServiceRequestPaintQuotationService2 {

	public GenericResponse<Map<String, Object>> duplicateQuotationToRepo2(WorkOrderRequest workOrderRequest,RegSource regSource);
	public Page<ServiceRequestHeaderAllQuotation2> getHeaderWorkOrder(String workOrderId, String requestId, 
	        String fromDate, String toDate, String spId, String status, Pageable pageable);
	
	public List<ServiceRequestPaintQuotation2> getWorkOrderDetails(
	        String admintasklineId,
	        String taskDescription,
	        String serviceCategory,
	        String taskId,
	        String measureNames,
	        String status,
	        String spId,
	        String requestId,
	        String workOrderId);
	
	public List<ServiceRequestPaintQuotation2> updateWorkOrderQuotation(String taskId,
			List<ServiceRequestPaintQuotation2> dtoList, RegSource regSource) ;
	
	public ResponseGetWorkOrderSRHdrAndCustomerDto getWorkOrderWithCustomerDetails(
	        String workOrderId, String quotationId, String fromQuotatedDate, String toQuotatedDate,
	        String status, String spId, String userid, String userEmail, String userMobile, Pageable pageable);

}
