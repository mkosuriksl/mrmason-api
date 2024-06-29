package com.application.mrmason.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.application.mrmason.dto.ResponseSpServiceDetailsDto;
import com.application.mrmason.dto.ResponseSpServiceGetDto;
import com.application.mrmason.dto.SpServiceDetailsDto;
import com.application.mrmason.entity.SpServiceDetails;
import com.application.mrmason.entity.User;
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
	public ResponseSpServiceDetailsDto updateServiceRequest(SpServiceDetails service) {
		SpServiceDetails data = serviceRepo.findByUserServicesId(service.getUserServicesId());
		if (data != null) {
			data.setAvailableWithinRange(service.getAvailableWithinRange());
			data.setCharge(service.getCharge());
			data.setPincode(service.getPincode());
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
	public List<User> getServicePersonDetails(String serviceType) {
		List<SpServiceDetails> serviceDetails = serviceRepo.findByServiceType(serviceType);
		if (!serviceDetails.isEmpty()) {
			List<String> userIds = serviceDetails.stream().map(SpServiceDetails::getUserId)
					.collect(Collectors.toList());

			List<User> users = userDAO.findByBodSeqNoIn(userIds);
			return users;
		}
		return Collections.emptyList();
	}

	@Override
	public List<SpServiceDetails> getUserService(String serviceType) {
		return serviceRepo.findByServiceType(serviceType);
	}

}
