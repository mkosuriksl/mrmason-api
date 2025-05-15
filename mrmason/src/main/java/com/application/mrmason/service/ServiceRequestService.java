package com.application.mrmason.service;

import java.util.List;
import java.util.Map;

import com.application.mrmason.entity.ServiceRequest;

public interface ServiceRequestService {
	ServiceRequest addRequest(ServiceRequest request);
//	List<ServiceRequest> getServiceReq(String userId,String assetId,String location,String serviceName,String email,String mobile,String status,String fromDate,String toDate);
	List<ServiceRequest> getServiceReq(String userId,String assetId,String location,String serviceName,String email,String mobile,String status,String fromDate,String toDate);
	ServiceRequest updateRequest(ServiceRequest requestData);
	ServiceRequest updateStatusRequest(ServiceRequest requestData);

	
//	public ServiceRequest requestedDetails(String requestId);
	
//	public void sendEmail(String toMail, ServiceRequest serivce);
	
//	public boolean sendEmail(String requestedBy, ServiceRequest service);
}
