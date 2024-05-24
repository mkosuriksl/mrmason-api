package com.application.mrmason.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.application.mrmason.dto.ResponseListServiceRequestDto;
import com.application.mrmason.dto.ResponseServiceReqDto;
import com.application.mrmason.dto.ServiceRequestDto;
import com.application.mrmason.entity.ServiceRequest;
import com.application.mrmason.service.ServiceRequestService;

@RestController
@PreAuthorize("hasAuthority('EC')")
public class ServiceRequestController {
	@Autowired
	ServiceRequestService reqService;
	ResponseListServiceRequestDto response=new ResponseListServiceRequestDto();
	
	@PostMapping("/addServiceRequest")
	public ResponseEntity<?> addRequest(@RequestBody ServiceRequest request){
		try {
			ResponseServiceReqDto response=new ResponseServiceReqDto();
			if(reqService.addRequest(request)!=null) {	
				response.setAddService(reqService.addRequest(request));
				response.setMessage("Service request added successfully..");
				response.setStatus(true);
				return new ResponseEntity<>(response,HttpStatus.OK);
			}else {
				response.setMessage("Invalid User.!");
				response.setStatus(false);
				return new ResponseEntity<>(response,HttpStatus.OK);
			}
		}catch(Exception e) {
			e.getMessage();
			return 	new ResponseEntity<>("Invalid User.!",HttpStatus.OK);
		}
	}
	@GetMapping("/getServiceRequest")
	public ResponseEntity<?> getRequest(@RequestBody ServiceRequestDto request){
		try {
			if(reqService.getServiceReq(request).isEmpty()) {	
				response.setMessage("No data found for the given details.!");
				response.setData(reqService.getServiceReq(request));
				response.setStatus(true);
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
			response.setData(reqService.getServiceReq(request));
			response.setMessage("ServiceRequest data fetched successfully..");
			response.setStatus(true);
			return ResponseEntity.ok(response);
			
		}catch(Exception e) {
			response.setMessage(e.getMessage());
			response.setStatus(false);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}
}
