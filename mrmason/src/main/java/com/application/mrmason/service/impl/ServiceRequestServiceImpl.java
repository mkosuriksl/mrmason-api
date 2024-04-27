package com.application.mrmason.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.application.mrmason.dto.ServiceRequestDto;
import com.application.mrmason.entity.CustomerAssets;
import com.application.mrmason.entity.CustomerRegistration;
import com.application.mrmason.entity.ServiceRequest;
import com.application.mrmason.repository.CustomerAssetsRepo;
import com.application.mrmason.repository.CustomerRegistrationRepo;
import com.application.mrmason.repository.ServiceRequestRepo;
import com.application.mrmason.service.ServiceRequestService;

@Service
public class ServiceRequestServiceImpl implements ServiceRequestService{
    @Autowired
	public ServiceRequestRepo requestRepo;
    @Autowired
    public CustomerAssetsRepo assetRepo;
    @Autowired
	public CustomerRegistrationRepo repo;
	
	@Override
	public ServiceRequest addRequest(ServiceRequest requestData) {
		Optional<CustomerAssets> serviceRequestData=assetRepo.findByUserIdAndAssetId(requestData.getRequestedBy(), requestData.getAssetId());
		if(serviceRequestData.isPresent()) {
			return requestRepo.save(requestData);
		}
		return null;
	}

	

	@Override
	public List<ServiceRequest>  getServiceReq(ServiceRequestDto request) {
		String userId=request.getRequestedBy();
		String assetId=request.getAssetId();
		String location=request.getLocation();
		String serviceName=request.getServiceName();
		String email=request.getEmail();
		String status=request.getStatus();
		if(userId!=null && assetId==null && location==null && serviceName==null && email==null && status==null) {
			Optional<List<ServiceRequest>> user=Optional.of((requestRepo.findByRequestedByOrderByReqSeqIdDesc(userId)));
			return user.get();
		}else if(userId==null && assetId!=null && location==null && serviceName==null && email==null && status==null) {
			Optional<List<ServiceRequest>> user=Optional.of((requestRepo.findByAssetIdOrderByReqSeqIdDesc(assetId)));
			return user.get();
		}else if(userId==null && assetId==null && location!=null && serviceName==null &&  email==null && status==null) {
			Optional<List<ServiceRequest>> user=Optional.of((requestRepo.findByLocationOrderByReqSeqIdDesc(location)));
			return user.get();
		}else if(userId==null && assetId==null && location==null && serviceName!=null &&  email==null && status==null) {
			Optional<List<ServiceRequest>> user=Optional.of((requestRepo.findByServiceNameOrderByReqSeqIdDesc(serviceName)));
			return user.get();
		}else if(userId==null && assetId==null && location==null && serviceName==null &&  email!=null && status==null) {
			Optional<CustomerRegistration> existedById = Optional.of(repo.findByUserEmail(email));
			if(existedById.isPresent()) {
				Optional<List<ServiceRequest>> user=Optional.of((requestRepo.findByRequestedByOrderByReqSeqIdDesc(existedById.get().getUserid())));
				return user.get();
			}
		}else if(userId==null && assetId==null && location==null && serviceName==null &&  email==null && status!=null) {
			Optional<List<ServiceRequest>> user=Optional.of((requestRepo.findByStatusOrderByReqSeqIdDesc(status)));
			return user.get();
		}
		return null;
	}
	
}
