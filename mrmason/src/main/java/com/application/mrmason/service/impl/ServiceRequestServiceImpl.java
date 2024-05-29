package com.application.mrmason.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
	public List<ServiceRequest>  getServiceReq(String userId,String assetId,String location,String serviceName,String email,String mobile,String status,String fromDate,String toDate) {
		
		if(userId!=null && assetId==null && location==null && serviceName==null && email==null && status==null&& mobile==null) {
			List<ServiceRequest> user=requestRepo.findByRequestedByOrderByReqSeqIdDesc(userId);
			return user;
		}else if(userId==null && assetId!=null && location==null && serviceName==null && email==null && status==null&& mobile==null) {
			Optional<List<ServiceRequest>> user=Optional.of((requestRepo.findByAssetIdOrderByReqSeqIdDesc(assetId)));
			return user.get();
		}else if(userId==null && assetId==null && location!=null && serviceName==null &&  email==null && status==null&& mobile==null) {
			Optional<List<ServiceRequest>> user=Optional.of((requestRepo.findByLocationOrderByReqSeqIdDesc(location)));
			return user.get();
		}else if(userId==null && assetId==null && location==null && serviceName!=null &&  email==null && status==null&& mobile==null) {
			Optional<List<ServiceRequest>> user=Optional.of((requestRepo.findByServiceNameOrderByReqSeqIdDesc(serviceName)));
			return user.get();
		}else if(userId==null && assetId==null && location==null && serviceName==null &&  email!=null || mobile!=null && status==null ) {
			Optional<CustomerRegistration> existedById = Optional.ofNullable(repo.findByUserEmailOrUserMobile(email, mobile));
			if(existedById.isPresent()) {
				Optional<List<ServiceRequest>> user=Optional.of((requestRepo.findByRequestedByOrderByReqSeqIdDesc(existedById.get().getUserid())));
				return user.get();
			}
		}else if(userId==null && assetId==null && location==null && serviceName==null &&  email==null && status!=null&& mobile==null) {
			Optional<List<ServiceRequest>> user=Optional.of((requestRepo.findByStatusOrderByReqSeqIdDesc(status)));
			return user.get();
		}else if(userId==null && assetId==null && location==null && serviceName==null &&  email==null && status==null&& mobile==null&&fromDate!=null&& toDate!=null) {
			Optional<List<ServiceRequest>> user=Optional.of((requestRepo.findByServiceRequestDateBetween(fromDate, toDate)));
			return user.get();
		}
		return null;
	}
	
}
