package com.application.mrmason.service;

import java.util.List;

import com.application.mrmason.dto.ResponseSpServiceDetailsDto;
import com.application.mrmason.dto.ResponseSpServiceGetDto;
import com.application.mrmason.dto.SpServiceDetailsDto;
import com.application.mrmason.entity.AddServices;
import com.application.mrmason.entity.AdminServiceName;
import com.application.mrmason.entity.SpServiceDetails;
import com.application.mrmason.entity.User;

public interface SpServiceDetailsService {
	ResponseSpServiceDetailsDto addServiceRequest(SpServiceDetails service);

	ResponseSpServiceGetDto getServiceRequest(String userId, String serviceType, String serviceId);

	ResponseSpServiceDetailsDto updateServiceRequest(SpServiceDetails service);

	SpServiceDetailsDto getDto(String userServicesId);
	
	List<SpServiceDetails> getUserService(String serviceType,String location);
	List<User> getServicePersonDetails(String serviceType,String location);
	
	List<AddServices> getUserInDetails(String serviceType, String location);
	
	List<AdminServiceName> getServiceNames(String serviceType, String location);
}
