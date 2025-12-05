package com.application.mrmason.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.application.mrmason.dto.ResponseUserServiceChargesDto;
import com.application.mrmason.dto.ResponseUserServiceChargesDto1;
import com.application.mrmason.dto.ResponseUserServiceChargesDto2;
import com.application.mrmason.dto.UserChargeResponseDTO;
import com.application.mrmason.dto.UserServiceChargeRequest;
import com.application.mrmason.entity.UserServiceCharges;
import com.application.mrmason.exceptions.ResourceNotFoundException;
import com.application.mrmason.service.UserServiceChargesService;

@RestController
@PreAuthorize("hasAuthority('Adm')")
public class UserServiceChargesController {

	@Autowired
	UserServiceChargesService service;

	@PostMapping("/adding-UserServiceCharges")
	public ResponseEntity<ResponseUserServiceChargesDto> userServiceCharges(
			@RequestBody UserServiceChargeRequest serviceChargeRequest) {
		ResponseUserServiceChargesDto response = new ResponseUserServiceChargesDto();
		List<UserServiceCharges> savedCharges = service.addCharges(serviceChargeRequest);

		try {
			if (!savedCharges.isEmpty()) {
				response.setMessage("Added service charges");
				response.setStatus(true);
				response.setServiceChargesData(savedCharges);
				return new ResponseEntity<>(response, HttpStatus.OK);
			} else {
//				response.setMessage("No new charges were added/failed to add charges");
				response.setMessage("Same fields/locations are already record in User Service Charges");
				response.setStatus(false);
				response.setServiceChargesData(savedCharges);
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
		} catch (Exception e) {
			response.setMessage(e.getMessage());
			response.setStatus(false);
			response.setServiceChargesData(savedCharges);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}

	@GetMapping("/getUserServiceCharegs")
	public ResponseEntity<?> getUserCharges(@RequestParam(required = false) String serviceChargeKey,
			@RequestParam(required = false) String serviceId, @RequestParam(required = false) String location,
			@RequestParam(required = false) String brand, @RequestParam(required = false) String model,
			@RequestParam(required = false) String userId, @RequestParam(required = false) String subcategory) {

		if (userId == null || userId.isBlank() || userId.isEmpty()) {
			throw new ResourceNotFoundException("User Id is required.");
		}

		ResponseUserServiceChargesDto1 response = new ResponseUserServiceChargesDto1();
		try {

			List<UserServiceCharges> serviceCharges = service.getUserServiceCharges(serviceChargeKey, serviceId,
					location, brand, model, userId, subcategory);
			if (serviceCharges != null && !serviceCharges.isEmpty()) {
				response.setMessage("User service charges details");
				response.setStatus(true);
				response.setGetData(serviceCharges);
				return new ResponseEntity<>(response, HttpStatus.OK);
			} else {
				response.setMessage("No details found for given parameters/check your parameters");
				response.setStatus(false);
				response.setGetData(serviceCharges);
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.OK).body(e.getMessage());
		}

	}

	@GetMapping("/getUserServiceCharegs-withoutSecurity")
	public ResponseEntity<?> getUserCharge(@RequestParam(required = false) String serviceChargeKey,
			@RequestParam(required = false) String serviceId, @RequestParam(required = false) String location,
			@RequestParam(required = false) String brand, @RequestParam(required = false) String model,
			@RequestParam String userId, @RequestParam(required = false) String subcategory) {

		List<UserChargeResponseDTO> response = service.getUserCharges(serviceChargeKey, serviceId, location, brand,
				model, userId, subcategory);

		if (response == null) {
			return ResponseEntity.ok("No Records Found");
		}

		return ResponseEntity.ok(response);
	}

	@PutMapping("/update-UserServiceCharges")
	public ResponseEntity<?> updateUserServiceCharges(@RequestBody UserServiceCharges serviceCharge) {
		ResponseUserServiceChargesDto2 response = new ResponseUserServiceChargesDto2();
		UserServiceCharges services = service.updateCharges(serviceCharge);
		try {
			if (services != null) {
				response.setMessage("service charges updated successfully");
				response.setStatus(true);
				response.setUpdatedData(services);
				return new ResponseEntity<>(response, HttpStatus.OK);
			} else {
				response.setMessage("failed to updated/servicechargekey not present");
				response.setStatus(true);
				response.setUpdatedData(services);
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.OK).body(e.getMessage());
		}

	}

}
