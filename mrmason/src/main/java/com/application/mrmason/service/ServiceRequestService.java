package com.application.mrmason.service;

import org.springframework.data.domain.Page;

import com.application.mrmason.entity.ServiceRequest;
import com.application.mrmason.enums.RegSource;

public interface ServiceRequestService {
//	ServiceRequest addRequest(ServiceRequest request);
	public Object addRequest(ServiceRequest requestData,RegSource regSource);

	ServiceRequest updateRequest(ServiceRequest requestData);

	ServiceRequest updateStatusRequest(ServiceRequest requestData);

	public Page<ServiceRequest> getServiceReq(String userId, String assetId, String location, String serviceSubCategory,
			String email, String mobile, String status, String fromDate, String toDate, int page, int size,
			RegSource regSource);
}
