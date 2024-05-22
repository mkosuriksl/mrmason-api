package com.application.mrmason.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.application.mrmason.dto.AddServiceGetDto;
import com.application.mrmason.dto.ResponseAddServiceDto;
import com.application.mrmason.dto.ResponseAddServiceGetDto;
import com.application.mrmason.dto.ResponseServiceReportDto;
import com.application.mrmason.entity.AddServices;
import com.application.mrmason.entity.User;
import com.application.mrmason.repository.UserDAO;
import com.application.mrmason.service.impl.AddServicesServiceIml;
import com.application.mrmason.service.impl.SPAvailabilityServiceIml;
import com.application.mrmason.service.impl.UserService;

@RestController
@PreAuthorize("hasAuthority('Developer')")
public class AddServiceController {

	@Autowired
	AddServicesServiceIml service;

	@Autowired
	UserService userService;

	@Autowired
	UserDAO userDAO;

	@Autowired
	SPAvailabilityServiceIml spAvailibilityImpl;

	ResponseAddServiceDto response= new ResponseAddServiceDto();
	
	@PostMapping("/add-service")
	public ResponseEntity<?> addService(@RequestBody AddServices add) {
		try {
			String bodSeqNo = add.getBodSeqNo();
			AddServices addedService = service.addServicePerson(add, bodSeqNo);
			if (addedService != null) {
				response.setMessage("Service added successfully");
				response.setStatus(true);
				response.setAddServicesData(addedService);
				return ResponseEntity.status(HttpStatus.OK).body(response);
			} else {
				response.setMessage("Failed to add service");
				response.setStatus(false);
				return ResponseEntity.status(HttpStatus.OK).body(response);
			}
		} catch (Exception e) {
            response.setMessage("Record already exists");
			return ResponseEntity.status(HttpStatus.OK).body(response);
		}
	}

	@PutMapping("/sp-add-services-update")
	public ResponseEntity<?> updateAddServices(@RequestBody AddServiceGetDto update) {
		try {
			String userIdServiceId = update.getUserIdServiceId();
			String serviceSubCategory = update.getServiceSubCategory();
			String bodSeqNo = update.getBodSeqNo();
			AddServices upServices = service.updateAddServiceDetails(update, userIdServiceId, serviceSubCategory,
					bodSeqNo);
			if (upServices == null) {
				response.setMessage("inavalid user");
				response.setStatus(false);
				return new ResponseEntity<>(response, HttpStatus.OK);

			} else {
				
				response.setMessage("Profile updated successfully");
				response.setStatus(true);
				response.setAddServicesData(upServices);
				return ResponseEntity.ok().body(response);
			}
		} catch (Exception e) {
			response.setStatus(false);
			response.setMessage(e.getMessage());
			return ResponseEntity.status(HttpStatus.OK).body(response);
		}

	}

	@GetMapping("/sp-user-services-get")
	public ResponseEntity<?> getServices(@RequestBody AddServiceGetDto get) {

		String serviceSubCategory = get.getServiceSubCategory();
		String bodSeqNo = get.getBodSeqNo();
		String useridServiceId = get.getUserIdServiceId();
		List<AddServices> getService = service.getPerson(bodSeqNo, serviceSubCategory, useridServiceId);
		ResponseAddServiceGetDto responseGet= new ResponseAddServiceGetDto();
		try {
			if (getService == null) {
				responseGet.setMessage("No services found for the given parameters");
				responseGet.setStatus(false);
				return new ResponseEntity<>(responseGet, HttpStatus.OK);
			} else if (getService.isEmpty()) {
				responseGet.setMessage("Invalid user......!");
				responseGet.setStatus(false);
				return new ResponseEntity<>(responseGet, HttpStatus.OK);
			} else {
				responseGet.setMessage("AddService details");
				responseGet.setStatus(true);
				responseGet.setGetAddServicesData(getService);
				return new ResponseEntity<>(responseGet, HttpStatus.OK);
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.OK).body(e.getMessage());
		}
	}

	@GetMapping("/sp-user-report")
	public ResponseEntity<?> getService(@RequestBody AddServiceGetDto get) {

		String bodSeqNo = get.getBodSeqNo();
		ResponseServiceReportDto serviceReport = new ResponseServiceReportDto();
		Optional<User> user = Optional.of(userDAO.findByBodSeqNo(bodSeqNo));
		try {
			if (userService.getServiceProfile(user.get().getEmail()) != null) {
				serviceReport.setRegData(userService.getServiceProfile(user.get().getEmail()));
				serviceReport.setMessage("success");
				serviceReport.setStatus(true);
				serviceReport.setServData(service.getPerson(bodSeqNo, null, null));
				serviceReport.setAvailData(spAvailibilityImpl.getAvailability(user.get().getEmail(), bodSeqNo));
				return new ResponseEntity<>(serviceReport, HttpStatus.OK);
			}
			serviceReport.setMessage("Invalid user.!");
			serviceReport.setStatus(false);
			return ResponseEntity.status(HttpStatus.OK).body(serviceReport);

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.OK).body(e.getMessage());
		}

	}
}
