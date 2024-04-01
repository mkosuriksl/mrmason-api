package com.application.mrmason.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.application.mrmason.dto.ResponseServiceReqDto;
import com.application.mrmason.dto.ServiceRequestDto;
import com.application.mrmason.entity.ServiceRequest;
import com.application.mrmason.service.ServiceRequestService;

@RestController
public class ServiceRequestController {
	@Autowired
	ServiceRequestService reqService;
	
	@PostMapping("/addServiceRequest")
	public ResponseEntity<?> addRequest(@RequestBody ServiceRequest request){
		try {
			if(reqService.addRequest(request)!=null) {	
				ResponseServiceReqDto response=new ResponseServiceReqDto();
				response.setAddService(reqService.addRequest(request));
				response.setMessage("Service request added successfully..");
				return new ResponseEntity<>(response,HttpStatus.OK);
			}else {
				return new ResponseEntity<>("Invalid user..!",HttpStatus.BAD_REQUEST);
			}
		}catch(Exception e) {
			e.getMessage();
			return 	new ResponseEntity<>("Invalid User.!",HttpStatus.NOT_FOUND);
		}
	}
	@GetMapping("/getServiceRequest")
	public ResponseEntity<?> getRequest(@RequestBody ServiceRequestDto request){
		try {
			if(reqService.getServiceReq(request).isEmpty()) {	
				return new ResponseEntity<>("Invalid user..!",HttpStatus.BAD_REQUEST);
			}
			return new ResponseEntity<>(reqService.getServiceReq(request),HttpStatus.OK);
			
		}catch(Exception e) {
			e.getMessage();
			return 	new ResponseEntity<>("Invalid User.!",HttpStatus.NOT_FOUND);
		}
	}
}
