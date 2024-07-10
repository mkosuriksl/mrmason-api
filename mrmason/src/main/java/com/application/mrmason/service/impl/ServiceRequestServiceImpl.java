package com.application.mrmason.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.application.mrmason.entity.CustomerAssets;
import com.application.mrmason.entity.CustomerRegistration;
import com.application.mrmason.entity.ServiceRequest;
import com.application.mrmason.entity.ServiceStatusUpate;
import com.application.mrmason.repository.CustomerAssetsRepo;
import com.application.mrmason.repository.CustomerRegistrationRepo;
import com.application.mrmason.repository.ServiceRequestRepo;
import com.application.mrmason.repository.ServiceStatusUpateRepo;
import com.application.mrmason.service.ServiceRequestService;

@Service
public class ServiceRequestServiceImpl implements ServiceRequestService {
	@Autowired
	ServiceRequestRepo requestRepo;
	@Autowired
	public CustomerAssetsRepo assetRepo;
	@Autowired
	public CustomerRegistrationRepo repo;
	@Autowired
	ServiceStatusUpateRepo statusRepo;

	ServiceStatusUpate statusUpdate = new ServiceStatusUpate();
	@Autowired
	ModelMapper model;

	@Autowired
	CustomerRegistrationRepo Customerrepo;

	@Autowired
	private JavaMailSender mailsender;


	
	@Override
	public ServiceRequest addRequest(ServiceRequest requestData) {
		Optional<CustomerAssets> serviceRequestData = assetRepo.findByUserIdAndAssetId(requestData.getRequestedBy(),
				requestData.getAssetId());
		if (serviceRequestData.isPresent()) {
			ServiceRequest service = requestRepo.save(requestData);
			statusUpdate.setServiceRequestId(service.getRequestId());
			statusUpdate.setUpdatedBy(service.getRequestedBy());
			statusRepo.save(statusUpdate);
			return service;
		}
		return null;
	}

	@Override
	public List<ServiceRequest> getServiceReq(String userId, String assetId, String location, String serviceName,
			String email, String mobile, String status, String fromDate, String toDate) {
		if (userId != null) {
			return requestRepo.findByRequestedByOrderByServiceRequestDateDesc(userId);
		} else if (assetId != null) {
			return requestRepo.findByAssetIdOrderByServiceRequestDateDesc(assetId);
		} else if (location != null) {
			return requestRepo.findByLocationOrderByServiceRequestDateDesc(location);
		} else if (serviceName != null) {
			return requestRepo.findByServiceSubCategoryOrderByServiceRequestDateDesc(serviceName);
		} else if (email != null || mobile != null) {
			CustomerRegistration customer = repo.findByUserEmailOrUserMobile(email, mobile);
			if (customer != null) {
				return requestRepo.findByRequestedByOrderByServiceRequestDateDesc(customer.getUserid());
			}
		} else if (status != null) {
			return requestRepo.findByStatusOrderByServiceRequestDateDesc(status);
		} else if (fromDate != null && toDate != null) {
			return requestRepo.findByServiceRequestDateBetween(fromDate, toDate);
		}
		return Collections.emptyList();
	}

	@Override
	public ServiceRequest updateRequest(ServiceRequest requestData) {
		ServiceRequest serviceRequestData = requestRepo.findByRequestId(requestData.getRequestId());
		if (serviceRequestData != null) {
//			ServiceRequest service= model.map(serviceRequestData, ServiceRequest.class);
			serviceRequestData.setDescription(requestData.getDescription());
			serviceRequestData.setLocation(requestData.getLocation());
			serviceRequestData.setServiceSubCategory(requestData.getServiceSubCategory());
			return requestRepo.save(serviceRequestData);
		}
		return null;
	}

	@Override
	public ServiceRequest updateStatusRequest(ServiceRequest requestData) {
		ServiceRequest serviceRequestData = requestRepo.findByRequestId(requestData.getRequestId());
		if (serviceRequestData != null) {
			ServiceStatusUpate update = statusRepo.findByServiceRequestId(requestData.getRequestId());
			if (update != null) {
				serviceRequestData.setStatus(requestData.getStatus());
				ServiceRequest service = requestRepo.save(serviceRequestData);

				update.setStatus(requestData.getStatus());
				statusRepo.save(update);
				return service;
			}

		}
		return null;
	}

//	@Override
//	public boolean sendEmail(String requestedBy, ServiceRequest service) {
//		Optional<ServiceRequest> request = Optional.ofNullable(requestRepo.findByRequestId(service.getRequestId()));
//		if (request.isPresent()) {
//			String requestedByEmail = request.get().getRequestedBy();
//			CustomerRegistration customer = Customerrepo.findByUserEmailCustomQuery(requestedByEmail);
//			if (customer == null || customer.getUserEmail() == null) {
//				return false;
//			}
//
//			String email = customer.getUserEmail();
//			SimpleMailMessage mail = new SimpleMailMessage();
//			mail.setTo(email);
//			mail.setSubject("Your request details.");
//			String body = String.format(
//					"ReqSeqId: %s\nAssetId: %s\nRequestId: %s\nServiceName: %s\nService sub category: %s\nRequestedBy: %s\nStatus: %s\nServiceDate: %s\nDescription: %s\nLocation: %s",
//					service.getReqSeqId(), service.getAssetId(), service.getRequestId(), service.getServiceName(),
//					service.getServiceSubCategory(), service.getRequestedBy(), service.getStatus(),
//					service.getServiceDateDb(), service.getDescription(), service.getLocation());
//			mail.setText(body);
//			mailsender.send(mail);
//			return true;
//		}
//		return false;
//	}

}
