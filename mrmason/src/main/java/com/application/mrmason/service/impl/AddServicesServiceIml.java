package com.application.mrmason.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.application.mrmason.dto.AddServiceGetDto;
import com.application.mrmason.dto.AddServicesDto;
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


	
	public List<AddServicesDto> getAddServicesWithServiceNames(String bodSeqNo, String serviceSubCategory, String useridServiceId) {
	    List<AddServices> servicesList;

	    if (bodSeqNo == null && serviceSubCategory != null && useridServiceId == null) {
	        servicesList = repo.findByServiceSubCategory(serviceSubCategory);
	    } else if (bodSeqNo != null && serviceSubCategory == null && useridServiceId == null) {
	        servicesList = repo.findByBodSeqNo(bodSeqNo);
	    } else if (bodSeqNo == null && serviceSubCategory == null && useridServiceId != null) {
	        servicesList = repo.getUserIdServiceIdDetails(useridServiceId);
	    } else {
	        servicesList = Collections.emptyList();
	    }

	    if (!servicesList.isEmpty()) {
	       
	        Set<String> serviceIds = new HashSet<>();
	        for (AddServices service : servicesList) {
	            if (service.getServiceId() != null) {
	                String[] ids = service.getServiceId().split(",");
	                Collections.addAll(serviceIds, ids);
	            }
	        }

	       
	        List<AdminServiceNameDto> serviceNameDtos = getServiceNamesByIds(new ArrayList<>(serviceIds));

	        
	        Map<String, String> serviceIdToNameMap = serviceNameDtos.stream()
	            .collect(Collectors.toMap(AdminServiceNameDto::getServiceId, AdminServiceNameDto::getServiceName));

	        
	        return servicesList.stream().map(service -> {
	            AddServicesDto dto = new AddServicesDto();
	            dto.setUserIdServiceId(service.getUserIdServiceId());
	            dto.setServiceId(service.getServiceId());
	            dto.setServiceSubCategory(service.getServiceSubCategory());
	            dto.setStatus(service.getStatus());
	            dto.setBodSeqNo(service.getBodSeqNo());
	            dto.setUpdatedBy(service.getUpdatedBy());
	            dto.setUpdatedDate(service.getUpdatedDate());
	            dto.setUpdateDateFormat(service.getUpdateDateFormat());
	            dto.setServiceIdList(Arrays.asList(service.getServiceId().split(",")));
	            dto.setServiceNameList(Arrays.stream(service.getServiceId().split(","))
	                .map(serviceIdToNameMap::get)
	                .collect(Collectors.toList()));
	            return dto;
	        }).collect(Collectors.toList());
	    }

	    return Collections.emptyList();
	}

	
	public List<AdminServiceNameDto> getServiceNamesByIds(List<String> serviceIds) {
	    
	    List<AdminServiceName> serviceCatDataList = serviceRepo.findByServiceIdIn(serviceIds);

	    
	    return serviceCatDataList.stream().map(serviceCatData -> {
	        AdminServiceNameDto serviceDto = new AdminServiceNameDto();
	        serviceDto.setServiceId(serviceCatData.getServiceId());
	        serviceDto.setServiceSubCat(serviceCatData.getServiceSubCategory());
	        serviceDto.setAddedBy(serviceCatData.getAddedBy());
	        serviceDto.setAddedDate(serviceCatData.getAddedDate());
	        serviceDto.setServiceName(serviceCatData.getServiceName());
	        return serviceDto;
	    }).collect(Collectors.toList());
	}

	
	
	
}
