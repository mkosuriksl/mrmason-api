package com.application.mrmason.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.application.mrmason.dto.ResponseSpServiceDetailsDto;
import com.application.mrmason.dto.ResponseSpServiceGetDto;
import com.application.mrmason.dto.SpServiceDetailsDto;
import com.application.mrmason.dto.Userdto;
import com.application.mrmason.entity.AddServices;
import com.application.mrmason.entity.AdminServiceName;
import com.application.mrmason.entity.SpServiceDetails;
import com.application.mrmason.entity.User;
import com.application.mrmason.repository.AddServiceRepo;
import com.application.mrmason.repository.AdminServiceNameRepo;
import com.application.mrmason.repository.SpServiceDetailsRepo;
import com.application.mrmason.repository.UserDAO;
import com.application.mrmason.service.SpServiceDetailsService;

@Service
public class SpServiceDetailsServiceImpl implements SpServiceDetailsService {
	@Autowired
	SpServiceDetailsRepo serviceRepo;
	@Autowired
	ModelMapper model;

	@Autowired
	UserDAO userDAO;

	@Autowired
	AddServiceRepo addServiceRepo;

	@Autowired
	AdminServiceNameRepo serviceNameRepo;

	ResponseSpServiceDetailsDto response = new ResponseSpServiceDetailsDto();
	ResponseSpServiceGetDto response2 = new ResponseSpServiceGetDto();
	@Autowired
	UserDAO userRepo;

	@Override
	public ResponseSpServiceDetailsDto addServiceRequest(SpServiceDetails service) {
		if (serviceRepo.findByUserServicesId(service.getUserServicesId()) == null) {
			if (userRepo.findByBodSeqNo(service.getUserId()) != null) {
				SpServiceDetails data = serviceRepo.save(service);
				SpServiceDetailsDto serviceDto = model.map(data, SpServiceDetailsDto.class);
				response.setMessage("SpServiceDeatails added successfully.");
				response.setStatus(true);
				response.setData(serviceDto);
				return response;
			}
		}
		return null;
	}

	@Override
	public ResponseSpServiceGetDto getServiceRequest(String userId, String serviceType, String serviceId) {
		List<SpServiceDetails> data = serviceRepo.findByUserIdOrServiceTypeOrUserServicesId(userId, serviceType,
				serviceId);
		if (!data.isEmpty()) {
			response2.setMessage("SpServiceDetails fetched successfully..");
			response2.setStatus(true);
			response2.setData(data);
			return response2;
		}
		response2.setMessage("No services found for the given details.!");
		response2.setStatus(true);
		response2.setData(data);
		return response2;
	}

	@Override
	public ResponseSpServiceGetDto getServices(String userId, List<String> serviceTypes, String serviceId) {
		List<SpServiceDetails> resp = new ArrayList<>();
		List<SpServiceDetails> data = serviceRepo.findByUserIdAndServiceTypesAndUserServiceId(userId, serviceTypes,
				serviceId);
		resp.addAll(data);
		if (!resp.isEmpty()) {
			response2.setMessage("SpServiceDetails fetched successfully..");
			response2.setStatus(true);
			response2.setData(resp);
			return response2;
		}
		response2.setMessage("No services found for the given details.!");
		response2.setStatus(true);
		response2.setData(resp);
		return response2;
	}

	@Override
	public ResponseSpServiceDetailsDto updateServiceRequest(SpServiceDetails service) {
		SpServiceDetails data = serviceRepo.findByUserServicesId(service.getUserServicesId());
		if (data != null) {
			data.setAvailableWithinRange(service.getAvailableWithinRange());
			data.setCharge(service.getCharge());
//			data.setPincode(service.getPincode());
			data.setLocation(service.getLocation());
			data.setCity(service.getCity());
			data.setExperience(service.getExperience());
			data.setQualification(service.getQualification());
			data.setServiceType(service.getServiceType());
			SpServiceDetails details = serviceRepo.save(service);
			SpServiceDetailsDto serviceDto = model.map(details, SpServiceDetailsDto.class);
			response.setMessage("SpServiceDeatails updated successfully.");
			response.setStatus(true);
			response.setData(serviceDto);
			return response;
		}
		return null;
	}

	@Override
	public SpServiceDetailsDto getDto(String userServicesId) {
		SpServiceDetails data = serviceRepo.findByUserServicesId(userServicesId);
		SpServiceDetailsDto serviceDto = model.map(data, SpServiceDetailsDto.class);
		return serviceDto;
	}

	@Override
	public List<Userdto> getServicePersonDetails(String serviceType, String location) {
		List<SpServiceDetails> serviceDetails;

		if (serviceType != null && location != null) {
			serviceDetails = serviceRepo.findByServiceTypeAndLocation(serviceType, location);
		} else if (serviceType != null && location == null) {
			serviceDetails = serviceRepo.findByServiceType(serviceType);
		} else if (location != null && serviceType == null) {
			serviceDetails = serviceRepo.findByLocation(location);
		} else {
			return Collections.emptyList();
		}

		if (!serviceDetails.isEmpty()) {
			List<String> userIds = serviceDetails.stream().map(SpServiceDetails::getUserId)
					.collect(Collectors.toList());
			List<User> users = userDAO.findByBodSeqNoIn(userIds);
			return users.stream().map(this::convertToUserDTO).collect(Collectors.toList());
		}
		return Collections.emptyList();
	}

	private Userdto convertToUserDTO(User user) {
		Userdto dto = new Userdto();
		dto.setBodSeqNo(user.getBodSeqNo());
		dto.setName(user.getName());
		dto.setBusinessName(user.getBusinessName());
		dto.setMobile(user.getMobile());
		dto.setEmail(user.getEmail());
		dto.setAddress(user.getAddress());
		dto.setCity(user.getCity());
		dto.setDistrict(user.getDistrict());
		dto.setState(user.getState());
		dto.setLocation(user.getLocation());
		dto.setUpdatedDate(user.getUpdatedDate());
		dto.setRegisteredDate(user.getRegisteredDate());
		dto.setVerified(user.getVerified());
		dto.setServiceCategory(user.getServiceCategory());
		dto.setUserType(String.valueOf(user.getUserType()));
		dto.setStatus(user.getStatus());

		return dto;
	}

	@Override
	public List<SpServiceDetails> getUserService(String serviceType, String location) {
		if (serviceType != null && location != null) {
			return serviceRepo.findByServiceTypeAndLocation(serviceType, location);
		} else if (serviceType != null && location == null) {
			return serviceRepo.findByServiceType(serviceType);
		} else if (location != null && serviceType == null) {
			return serviceRepo.findByLocation(location);
		} else {
			return Collections.emptyList();
		}
	}

	@Override
	public List<AddServices> getUserInDetails(String serviceType, String location) {
		List<SpServiceDetails> serviceDetails;

		if (serviceType != null && location != null) {
			serviceDetails = serviceRepo.findByServiceTypeAndLocation(serviceType, location);
		} else if (serviceType != null && location == null) {
			serviceDetails = serviceRepo.findByServiceType(serviceType);
		} else if (location != null && serviceType == null) {
			serviceDetails = serviceRepo.findByLocation(location);
		} else {
			System.out.println("Both serviceType and location are null.");
			return Collections.emptyList();
		}

		if (serviceDetails.isEmpty()) {
			System.out.println("No service details found for the given serviceType and/or location.");
			return Collections.emptyList();
		}

		List<String> userServicesId = serviceDetails.stream().map(SpServiceDetails::getUserServicesId)
				.collect(Collectors.toList());

		System.out.println("User IDs: " + userServicesId);

		List<AddServices> services = addServiceRepo.findByUserIdServiceIdIn(userServicesId);

		return services;
	}

	@Override
	public List<AdminServiceName> getServiceNames(String serviceType, String location) {
		List<SpServiceDetails> serviceDetails;

		if (serviceType != null && location != null) {
			serviceDetails = serviceRepo.findByServiceTypeAndLocation(serviceType, location);
		} else if (serviceType != null && location == null) {
			serviceDetails = serviceRepo.findByServiceType(serviceType);
		} else if (location != null && serviceType == null) {
			serviceDetails = serviceRepo.findByLocation(location);
		} else {
			return Collections.emptyList();
		}

		if (serviceDetails.isEmpty()) {
			return Collections.emptyList();
		}

		List<String> userServicesId = serviceDetails.stream().map(SpServiceDetails::getUserServicesId)
				.collect(Collectors.toList());

		List<AddServices> services = addServiceRepo.findByUserIdServiceIdIn(userServicesId);

		if (services.isEmpty()) {
			return Collections.emptyList();
		}

		Set<String> serviceIds = new HashSet<>();
		for (AddServices service : services) {
			if (service.getServiceId() != null) {
				String[] ids = service.getServiceId().split(",");
				Collections.addAll(serviceIds, ids);
			}
		}

		List<AdminServiceName> serviceNames = serviceNameRepo.findByServiceIdIn(new ArrayList<>(serviceIds));
		System.out.println("Service Names: " + serviceNames);

		return serviceNames;
	}

}
