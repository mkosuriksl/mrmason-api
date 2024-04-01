package com.application.mrmason.service;

import java.util.List;

import com.application.mrmason.dto.ServiceRequestDto;
import com.application.mrmason.entity.ServiceRequest;

public interface ServiceRequestService {
	ServiceRequest addRequest(ServiceRequest request);
	List<ServiceRequest> getServiceReq(ServiceRequestDto request);

}
