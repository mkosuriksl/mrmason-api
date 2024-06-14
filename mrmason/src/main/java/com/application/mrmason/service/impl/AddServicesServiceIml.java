package com.application.mrmason.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.application.mrmason.dto.AddServiceGetDto;
import com.application.mrmason.dto.AdminServiceNameDto;
import com.application.mrmason.entity.AddServices;
import com.application.mrmason.entity.AdminServiceName;
import com.application.mrmason.entity.User;
import com.application.mrmason.repository.AddServiceRepo;
import com.application.mrmason.repository.AdminServiceNameRepo;
import com.application.mrmason.repository.UserDAO;


@Service
public class AddServicesServiceIml {

	@Autowired
	AddServiceRepo repo;

	@Autowired
	UserDAO userDAO;

	@Autowired
	UserService userService;

	@Autowired
	AdminServiceNameRepo serviceRepo;
	
	public AddServices addServicePerson(AddServices add, String bodSeqNo) throws Exception {
		Optional<User> optionalUser = userDAO.findById(bodSeqNo);
		if (optionalUser.isPresent()) {
			User user = optionalUser.get();
			return repo.save(add);
		} else {

			throw new Exception("User not found for bodSeqNo: " + bodSeqNo);
		}

	}


	public AddServices updateAddServiceDetails(AddServiceGetDto services, String userIdServiceId, String serviceSubCategory, String bodSeqNo) {
		Optional<AddServices> optionalAdd = Optional.of(repo.findByUserIdServiceId(userIdServiceId));
		if (optionalAdd.isPresent()) {
			AddServices add = optionalAdd.get();
			add.setServiceId(services.getServiceId());
			add.setStatus(services.getStatus());
			return repo.save(add);
		}
		return null;
	}

	
	
	public List<AddServices> getPerson(String bodSeqNo, String serviceSubCategory, String useridServiceId) {
	    if (bodSeqNo == null && serviceSubCategory != null && useridServiceId == null) {
	        return repo.findByServiceSubCategory(serviceSubCategory);
	    } else if (bodSeqNo != null && serviceSubCategory == null && useridServiceId == null) {
	        return repo.findByBodSeqNo(bodSeqNo);
	    } else if (bodSeqNo == null && serviceSubCategory == null && useridServiceId != null) {
	        return repo.getUserIdServiceIdDetails(useridServiceId);
	    }
	    return Collections.emptyList(); 
	}

	public List<AdminServiceNameDto> getServiceById(String bodSeqNo, String serviceSubCategory, String useridServiceId) {
	    
	    List<AddServices> servicesList = getPerson(bodSeqNo, serviceSubCategory, useridServiceId);

	   
	    if (!servicesList.isEmpty()) {
	        List<String> serviceIds = servicesList.stream()
	                                              .map(AddServices::getServiceId)
	                                              .collect(Collectors.toList());
	        
	        List<AdminServiceName> serviceCatDataList = serviceRepo.findByServiceIdIn(serviceIds);
	       
	        List<AdminServiceNameDto> serviceDtoList = serviceCatDataList.stream().map(serviceCatData -> {
	            AdminServiceNameDto serviceDto = new AdminServiceNameDto();
	            serviceDto.setServiceId(serviceCatData.getServiceId());
	            serviceDto.setServiceSubCat(serviceCatData.getServiceSubCategory());
	            serviceDto.setAddedBy(serviceCatData.getAddedBy());
	            serviceDto.setAddedDate(serviceCatData.getAddedDate());
	            serviceDto.setServiceName(serviceCatData.getServiceName());
	            return serviceDto;
	        }).collect(Collectors.toList());

	        return serviceDtoList;
	    }

	    return Collections.emptyList();
	}


}
