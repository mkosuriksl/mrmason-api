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

	@PostMapping("/add-service")
	public ResponseEntity<String> addService(@RequestBody AddServices add) {
		try {
			String bodSeqNo = add.getBodSeqNo();
			AddServices addedService = service.addServicePerson(add, bodSeqNo);
			if (addedService != null) {
				return ResponseEntity.status(HttpStatus.CREATED).body("Service added successfully");
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to add service");
			}
		} catch (Exception e) {

			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Record already exists");
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
				return new ResponseEntity<>("inavalid user", HttpStatus.BAD_REQUEST);

			} else {
				String successMessage = "Profile updated successfully";
				return ResponseEntity.ok().body(successMessage);
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}

	}

	@GetMapping("/sp-user-services-get")
	public ResponseEntity<?> getServices(@RequestBody AddServiceGetDto get) {

		String serviceSubCategory = get.getServiceSubCategory();
		String bodSeqNo = get.getBodSeqNo();
		String useridServiceId = get.getUserIdServiceId();
		List<AddServices> getService = service.getPerson(bodSeqNo, serviceSubCategory, useridServiceId);

		try {
			if (getService == null) {
				return new ResponseEntity<>("No services found for the given parameters.", HttpStatus.NOT_FOUND);
			} else if (getService.isEmpty()) {
				return new ResponseEntity<>("Invalid user......!", HttpStatus.BAD_REQUEST);
			} else {
				return new ResponseEntity<>(getService, HttpStatus.OK);
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
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
				serviceReport.setMsge("success");
				serviceReport.setServData(service.getPerson(bodSeqNo, null, null));
				serviceReport.setAvailData(spAvailibilityImpl.getAvailability(user.get().getEmail(), bodSeqNo));
				return new ResponseEntity<>(serviceReport, HttpStatus.OK);
			}

			return null;

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}

	}
}
