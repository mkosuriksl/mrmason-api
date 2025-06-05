package com.application.mrmason.service;

import java.util.List;

import com.application.mrmason.dto.ResponseSpServiceDetailsDto;
import com.application.mrmason.dto.ResponseSpServiceGetDto;
import com.application.mrmason.dto.SpServiceDetailsDto;
import com.application.mrmason.dto.Userdto;
import com.application.mrmason.entity.AddServices;
import com.application.mrmason.entity.AdminServiceName;
import com.application.mrmason.entity.SpServiceDetails;
import com.application.mrmason.entity.SpServiceWithNoOfProject;

public interface SpServiceDetailsService {
//	ResponseSpServiceDetailsDto addServiceRequest(SpServiceDetails service);
	
	public ResponseSpServiceDetailsDto addServiceRequest(SpServiceDetailsDto requestDto) ;

	ResponseSpServiceGetDto getServiceRequest(String userId, String serviceType, String serviceId);
	
	ResponseSpServiceGetDto getServices(String userId, List<String> serviceType, String serviceId);

//	ResponseSpServiceDetailsDto updateServiceRequest(SpServiceDetails service);

	public ResponseSpServiceDetailsDto updateServiceRequest(SpServiceDetailsDto service) ;
	
	SpServiceDetailsDto getDto(String userServicesId);
	
	List<SpServiceDetails> getUserService(String serviceType,String location);
	
	public List<SpServiceWithNoOfProject> getByUserServicesId(String userServicesId);

    List<Userdto> getServicePersonDetails(String serviceType, String location);
	
	List<AddServices> getUserInDetails(String serviceType, String location);
	
	List<AdminServiceName> getServiceNames(String serviceType, String location);

	
	
}
