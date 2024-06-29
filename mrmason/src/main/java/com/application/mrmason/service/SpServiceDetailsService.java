package com.application.mrmason.service;

import java.util.List;

import com.application.mrmason.dto.ResponseSpServiceDetailsDto;
import com.application.mrmason.dto.ResponseSpServiceGetDto;
import com.application.mrmason.dto.SpServiceDetailsDto;
import com.application.mrmason.entity.SpServiceDetails;
import com.application.mrmason.entity.User;

public interface SpServiceDetailsService {
	ResponseSpServiceDetailsDto addServiceRequest(SpServiceDetails service);

	ResponseSpServiceGetDto getServiceRequest(String userId, String serviceType, String serviceId);

	ResponseSpServiceDetailsDto updateServiceRequest(SpServiceDetails service);

	SpServiceDetailsDto getDto(String userServicesId);

	List<User> getServicePersonDetails(String serviceType);

	List<SpServiceDetails> getUserService(String serviceType);
}
