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

import com.application.mrmason.dto.ResponseListSpServiceRequestDto;
import com.application.mrmason.dto.ResponseSpServiceRequestDto;
import com.application.mrmason.dto.SpServiceRequestDto;
import com.application.mrmason.entity.SpServiceRequest;
import com.application.mrmason.service.SpServiceRequestService;
@RestController
@PreAuthorize("hasAuthority('Developer')")
public class SpServiceRequestController {
	@Autowired
	SpServiceRequestService adminService;
	ResponseListSpServiceRequestDto response=new ResponseListSpServiceRequestDto();
	

	@PostMapping("/addSpServiceRequest")
	public ResponseEntity<ResponseSpServiceRequestDto> addServiceRequest(@RequestBody SpServiceRequest service) {
	    ResponseSpServiceRequestDto response = new ResponseSpServiceRequestDto();
	    try {
	        SpServiceRequestDto addedService = adminService.addServiceRequest(service);
	        if (addedService != null) {
	            response.setMessage("SP Service request added successfully..");
	            response.setData(addedService);
	            response.setStatus(true);
	            return ResponseEntity.ok(response);
	        }
	        response.setMessage("Invalid ServicePersonId or RequestId.!");
	        response.setStatus(false);
	        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
	    } catch (Exception e) {
	    	response.setMessage(e.getMessage());
	    	response.setStatus(false);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	    }
	}


	@GetMapping("/getSpServiceRequest")
	public ResponseEntity<?> getAssetDetails(@RequestBody SpServiceRequest service) {
		try {
			List<SpServiceRequest> entity = adminService.getServiceRequest(service);
			if (entity.isEmpty()) {
				response.setMessage("Invalid User.!");
				response.setStatus(false);
				return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
			}
			response.setData(entity);
			response.setMessage("SpServiceRequest data fetched successfully..");
			response.setStatus(true);
			return ResponseEntity.ok(response);

		} catch (Exception e) {
			response.setMessage(e.getMessage());
			response.setStatus(false);
			return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
		}

	}

	@PutMapping("/updateSpServiceRequest")
	public ResponseEntity<?> updateAssetDetails(@RequestBody SpServiceRequest service) {
		ResponseSpServiceRequestDto response = new ResponseSpServiceRequestDto();
		try {

			if (adminService.updateServiceRequest(service) != null) {

				response.setData(adminService.updateServiceRequest(service));
				response.setMessage("Service Request updated successfully..");
				response.setStatus(true);
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
			response.setMessage("Invalid User.!");
			response.setStatus(false);
			return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
		} catch (Exception e) {
			response.setMessage(e.getMessage());
			response.setStatus(false);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		}
	}
}

