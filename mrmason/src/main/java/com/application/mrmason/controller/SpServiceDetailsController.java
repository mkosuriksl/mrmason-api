package com.application.mrmason.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.application.mrmason.dto.GenericResponse;
import com.application.mrmason.dto.ResponseSpServiceDetailsDto;
import com.application.mrmason.dto.ResponseSpServiceGetDto;
import com.application.mrmason.dto.ResponseUserUserServicesDto;
import com.application.mrmason.dto.SpServiceDetailsDto;
import com.application.mrmason.dto.Userdto;
import com.application.mrmason.entity.AddServices;
import com.application.mrmason.entity.AdminServiceName;
import com.application.mrmason.entity.AdminSpVerification;
import com.application.mrmason.entity.SpServiceDetails;
import com.application.mrmason.entity.SpServiceWithNoOfProject;
import com.application.mrmason.entity.UploadUserProfileImage;
import com.application.mrmason.repository.AdminSpVerificationRepository;
import com.application.mrmason.service.SpServiceDetailsService;

@RestController
@PreAuthorize("hasAuthority('Developer')")
public class SpServiceDetailsController {
	@Autowired
	SpServiceDetailsService spService;
	
	@Autowired
	private AdminSpVerificationRepository verificationRepo;
	ResponseSpServiceDetailsDto response = new ResponseSpServiceDetailsDto();
	ResponseSpServiceGetDto response2 = new ResponseSpServiceGetDto();

	@PostMapping("/addSpService")
	public ResponseEntity<ResponseSpServiceDetailsDto> newAdminAsset(@RequestBody SpServiceDetailsDto sevice) {
		try {
			ResponseSpServiceDetailsDto data = spService.addServiceRequest(sevice);
			if (data != null) {
				return ResponseEntity.ok(data);
			}
			response.setMessage("Invalid details.!");
			response.setStatus(false);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setMessage(e.getMessage());
			response.setStatus(false);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}

	@GetMapping("/getSpService")
	public ResponseEntity<ResponseSpServiceGetDto> getAssetDetails(@RequestParam(required = false) String userId,
			@RequestParam(required = false) List<String> serviceTypes, @RequestParam(required = false) String servicesId) {
		try {
			ResponseSpServiceGetDto entity = spService.getServices(userId, serviceTypes, servicesId);
			return new ResponseEntity<>(entity, HttpStatus.OK);

		} catch (Exception e) {
			response.setMessage(e.getMessage());
			response.setStatus(false);
			return new ResponseEntity<>(response2, HttpStatus.OK);
		}

	}

	@PutMapping("/updateSpService")
	public ResponseEntity<ResponseSpServiceDetailsDto> updateAssetDetails(@RequestBody SpServiceDetailsDto service) {
		try {
			ResponseSpServiceDetailsDto data = spService.updateServiceRequest(service);
			if (data != null) {

				return new ResponseEntity<>(data, HttpStatus.OK);
			}
			response.setMessage("Invalid User.!");
			response.setStatus(false);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setMessage(e.getMessage());
			response.setStatus(false);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}

	@GetMapping("/getServicePersonDetails")
	public ResponseEntity<ResponseUserUserServicesDto> getServicePerson(
			@RequestParam(required = false) String serviceType, @RequestParam(required = false) String location) {

		List<Userdto> users = spService.getServicePersonDetails(serviceType, location);
		List<SpServiceDetails> userServices = spService.getUserService(serviceType, location);
		List<AddServices> userIndetail = spService.getUserInDetails(serviceType, location);
		List<AdminServiceName> serviceNames = spService.getServiceNames(serviceType, location);
	    List<SpServiceWithNoOfProject> noOfProjects = spService.getByUserServicesId(userServices);
	    List<UploadUserProfileImage> profileImage = spService.getByBodSeqNo(users);
	    List<AdminSpVerification>adminSpVerification=spService.getByVerifiedStatus(users);
		ResponseUserUserServicesDto response = new ResponseUserUserServicesDto();
		try {
			if (!userServices.isEmpty()) {
				response.setMessage("Received service person details");
				response.setStatus(true);
				response.setUserServicesData(userServices);
				response.setUserData(users);
				response.setUserServiceInDetail(userIndetail);
				response.setServiceNames(serviceNames);
				response.setNoOfProjects(noOfProjects);
				response.setProfilePhoto(profileImage);
				response.setAdminSPVerification(adminSpVerification);
				return new ResponseEntity<>(response, HttpStatus.OK);
			} else {
				response.setMessage("No details found. Check your serviceType/location");
				response.setStatus(false);
				response.setUserServicesData(userServices);
				response.setUserData(users);
				response.setUserServiceInDetail(userIndetail);
				response.setServiceNames(serviceNames);
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
		} catch (Exception e) {
			response.setMessage(e.getMessage());
			response.setStatus(false);
			response.setUserServicesData(userServices);
			response.setUserData(users);
			response.setUserServiceInDetail(userIndetail);
			response.setServiceNames(serviceNames);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}
	@GetMapping("/distinct-location-by-sp")
	public ResponseEntity<GenericResponse<List<String>>> autoSearchLocation(
	        @RequestParam(required = false) String serviceType,
	        @RequestParam(required = false) String location) {

	    List<String> locations = spService.getAutoSearchLocations(serviceType, location);

	    GenericResponse<List<String>> response = new GenericResponse<>();
	    if (!locations.isEmpty()) {
	        response.setSuccess(true);
	        response.setMessage("Matching locations found");
	        response.setData(locations);
	    } else {
	        response.setSuccess(false);
	        response.setMessage("No matching locations found");
	        response.setData(Collections.emptyList());
	    }

	    return ResponseEntity.ok(response);
	}

	
}
