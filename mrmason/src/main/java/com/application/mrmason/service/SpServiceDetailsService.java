package com.application.mrmason.service;

import com.application.mrmason.dto.ResponseSpServiceDetailsDto;
import com.application.mrmason.dto.ResponseSpServiceGetDto;
import com.application.mrmason.dto.SpServiceDetailsDto;
import com.application.mrmason.entity.SpServiceDetails;

public interface SpServiceDetailsService {
	ResponseSpServiceDetailsDto addServiceRequest(SpServiceDetails service);
	ResponseSpServiceGetDto getServiceRequest(SpServiceDetails service);
	ResponseSpServiceDetailsDto updateServiceRequest(SpServiceDetails service);
	SpServiceDetailsDto getDto(String userServicesId);
}
