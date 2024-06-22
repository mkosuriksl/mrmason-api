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
import com.application.mrmason.dto.AddServicesDto1;
import com.application.mrmason.dto.AdminServiceNameDto;
import com.application.mrmason.entity.AddServices;
import com.application.mrmason.entity.AdminServiceName;
import com.application.mrmason.entity.SpServiceDetails;
import com.application.mrmason.repository.AddServiceRepo;
import com.application.mrmason.repository.AdminServiceNameRepo;
import com.application.mrmason.repository.SpServiceDetailsRepo;
import com.application.mrmason.repository.UserDAO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;


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
	
	@Autowired
	SpServiceDetailsRepo spService;
	
	@PersistenceContext
	private EntityManager entityManager;

	
	public AddServicesDto1 addServices(AddServices service) {
	    System.out.println("Received userIdServiceId: " + service.getUserIdServiceId());
	    
	    Optional<SpServiceDetails> userIdServiceIdExists = Optional.ofNullable(spService.findByUserServicesId(service.getUserIdServiceId()));

	    if (userIdServiceIdExists.isPresent()) {
	        System.out.println("UserServiceId exists. Details: " + userIdServiceIdExists.get());

	        service.setBodSeqNo(userIdServiceIdExists.get().getUserId());
	        service.setUpdatedBy(userIdServiceIdExists.get().getUserId());
	        service.setStatus(userIdServiceIdExists.get().getStatus());
	        service.setUserIdServiceId(userIdServiceIdExists.get().getUserServicesId());

	        AddServices saved = repo.save(service);

	        AddServicesDto1 addSerices = new AddServicesDto1();
	        addSerices.setUserIdServiceId(saved.getUserIdServiceId());
	        addSerices.setBodSeqNo(saved.getBodSeqNo());
	        addSerices.setServiceId(saved.getServiceId());
	        addSerices.setServiceIdList(saved.getServiceIdList());
	        addSerices.setServiceSubCategory(saved.getServiceSubCategory());
	        addSerices.setStatus(saved.getStatus());
	        addSerices.setUpdateDateFormat(saved.getUpdateDateFormat());
	        addSerices.setUpdatedBy(saved.getUpdatedBy());
	        addSerices.setUpdatedDate(saved.getUpdatedDate());
	        addSerices.setUserServicesId(saved.getUserIdServiceId());

	        return addSerices;
	    } else {
	        System.out.println("UserServiceId does not exist.");
	    }

	    return null;
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

	
	
	public List<AddServices> getPerson(String bodSeqNo, String serviceSubCategory, String userIdServiceId) {

	    if (bodSeqNo == null && serviceSubCategory != null && userIdServiceId == null) {
	        return repo.findByServiceSubCategory(serviceSubCategory);
	    } else if (bodSeqNo != null && serviceSubCategory == null && userIdServiceId == null) {
	        return repo.findByBodSeqNo(bodSeqNo);
	    } else if (bodSeqNo == null && serviceSubCategory == null && userIdServiceId != null) {
	        return repo.getUserIdServiceIdDetails(userIdServiceId);
	    }
	    return Collections.emptyList(); 

	}



	

	
	public List<AddServicesDto> getAddServicesWithServiceNames(String bodSeqNo, String serviceSubCategory, String userIdServiceId) {
	    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
	    CriteriaQuery<AddServices> query = cb.createQuery(AddServices.class);
	    Root<AddServices> root = query.from(AddServices.class);
	    List<Predicate> predicates = new ArrayList<>();

	    if (bodSeqNo != null) {
	        predicates.add(cb.equal(root.get("bodSeqNo"), bodSeqNo));
	    }
	    if (serviceSubCategory != null) {
	        predicates.add(cb.equal(root.get("serviceSubCategory"), serviceSubCategory));
	    }
	    if (userIdServiceId != null) {
	        predicates.add(cb.equal(root.get("userIdServiceId"), userIdServiceId));
	    }

	    query.where(predicates.toArray(new Predicate[0]));

	    List<AddServices> servicesList = entityManager.createQuery(query).getResultList();
	    System.out.println("servicelist: " + servicesList);

	    if (!servicesList.isEmpty()) {
	        Set<String> serviceIds = new HashSet<>();
	        for (AddServices service : servicesList) {
	            if (service.getServiceId() != null) {
	                String[] ids = service.getServiceId().split(",");
	                Collections.addAll(serviceIds, ids);
	            }
	        }

	        List<AdminServiceNameDto> serviceNameDtos = getServiceNamesByIds(new ArrayList<>(serviceIds));
	        System.out.println("serviceNameDtos: " + serviceNameDtos);

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

	            if (service.getServiceId() != null) {
	                List<String> serviceIdList = Arrays.asList(service.getServiceId().split(","));
	                dto.setServiceIdList(serviceIdList);

	                // Filter out any service ID that does not have a corresponding name
	                List<String> validEntries = serviceIdList.stream()
	                    .filter(id -> serviceIdToNameMap.containsKey(id) && serviceIdToNameMap.get(id) != null)
	                    .map(id -> id + ":" + serviceIdToNameMap.get(id))
	                    .collect(Collectors.toList());

	                if (validEntries.isEmpty()) {
	                    dto.setServiceIdServiceName(""); // Case 1: All names are null
	                } else {
	                    dto.setServiceIdServiceName(String.join(", ", validEntries)); // Case 2 & 3: Some or all names are present
	                }
	            } else {
	                dto.setServiceIdList(Collections.emptyList());
	                dto.setServiceIdServiceName("");
	            }

	            return dto;
	        }).collect(Collectors.toList());
	    }

	    return Collections.emptyList();
	}

	public List<AdminServiceNameDto> getServiceNamesByIds(List<String> serviceIds) {
	    if (serviceIds == null || serviceIds.isEmpty()) {
	        System.err.println("serviceIds is null or empty");
	        return Collections.emptyList();
	    }

	    List<AdminServiceName> serviceCatDataList = serviceRepo.findByServiceIdIn(serviceIds);
	    System.out.println("serviceCatDataList: " + serviceCatDataList);

	    return serviceCatDataList.stream().map(serviceCatData -> {
	        AdminServiceNameDto serviceDto = new AdminServiceNameDto();
	        serviceDto.setServiceId(serviceCatData.getServiceId());
	        serviceDto.setServiceSubCategory(serviceCatData.getServiceSubCategory());
	        serviceDto.setAddedBy(serviceCatData.getAddedBy());
	        serviceDto.setAddedDate(serviceCatData.getAddedDate());
	        serviceDto.setServiceName(serviceCatData.getServiceName());
	        return serviceDto;
	    }).collect(Collectors.toList());
	}

	
}
