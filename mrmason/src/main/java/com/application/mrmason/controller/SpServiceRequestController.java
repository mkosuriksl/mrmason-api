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
import org.springframework.web.bind.annotation.RestController;

import com.application.mrmason.dto.ResponseSpServiceRequestDto;
import com.application.mrmason.dto.SpServiceRequestDto;
import com.application.mrmason.entity.SpServiceRequest;
import com.application.mrmason.service.SpServiceRequestService;
@RestController
@PreAuthorize("hasAuthority('Developer')")
public class SpServiceRequestController {
	@Autowired
	SpServiceRequestService adminService;

	@PostMapping("/addSpServiceRequest")
	public ResponseEntity<?> addServiceRequest(@RequestBody SpServiceRequest service) {
	    ResponseSpServiceRequestDto response = new ResponseSpServiceRequestDto();
	    try {
	        SpServiceRequestDto addedService = adminService.addServiceRequest(service);
	        if (addedService != null) {
	            response.setMessage("SP Service request added successfully..");
	            response.setData(addedService);
	            return ResponseEntity.ok(response);
	        }
	        return new ResponseEntity<>("Invalid ServicePersonId or RequestId.!", HttpStatus.UNAUTHORIZED);
	    } catch (Exception e) {
	        return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
	    }
	}


	@GetMapping("/getSpServiceRequest")
	public ResponseEntity<?> getAssetDetails(@RequestBody SpServiceRequest service) {
		try {
			List<SpServiceRequest> entity = adminService.getServiceRequest(service);
			if (entity.isEmpty()) {
				return new ResponseEntity<>("Invalid User.!", HttpStatus.UNAUTHORIZED);
			}
			return new ResponseEntity<>(entity, HttpStatus.OK);

		} catch (Exception e) {

			return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
		}

	}

	@PutMapping("/updateSpServiceRequest")
	public ResponseEntity<?> updateAssetDetails(@RequestBody SpServiceRequest service) {
		ResponseSpServiceRequestDto response = new ResponseSpServiceRequestDto();
		try {

			if (adminService.updateServiceRequest(service) != null) {

				response.setData(adminService.updateServiceRequest(service));
				response.setMessage("Service Request updated successfully..");
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
			return new ResponseEntity<>("Invalid User.!", HttpStatus.UNAUTHORIZED);
		} catch (Exception e) {

			return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
		}
	}
}

