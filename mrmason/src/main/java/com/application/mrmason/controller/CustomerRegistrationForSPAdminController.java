package com.application.mrmason.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.application.mrmason.dto.CustomerRegistrationRequestForSPAdmin;
import com.application.mrmason.dto.CustomerRegistrationRespForSPAdmin;
import com.application.mrmason.dto.CustomerRegistrationResponseForSPAdmin;
import com.application.mrmason.dto.ResponsesGetCustomerRegistrationDto;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.service.CustomerRegistrationForSPAdminService;

@RestController
public class CustomerRegistrationForSPAdminController {

	@Autowired
	private CustomerRegistrationForSPAdminService service;

	@PostMapping("/add-walk-in-customer-api-by-spOrAdmin")
	public ResponseEntity<CustomerRegistrationResponseForSPAdmin> registerCustomer(
			@RequestBody CustomerRegistrationRequestForSPAdmin dto,
			@RequestParam(required = false) RegSource regSource) {
		if (regSource == null) {
			regSource = RegSource.MRMASON;
		}
		return new ResponseEntity<>(service.registerCustomer(dto, regSource), HttpStatus.CREATED);
	}
	
	@PutMapping("/update-walk-in-customer-api-by-spOrAdmin")
	public ResponseEntity<CustomerRegistrationResponseForSPAdmin> updateCustomer(
			@RequestBody CustomerRegistrationRequestForSPAdmin dto,
			@RequestParam(required = false) RegSource regSource) {
		if (regSource == null) {
			regSource = RegSource.MRMASON;
		}
		return new ResponseEntity<>(service.updateCustomer(dto, regSource), HttpStatus.CREATED);
	}
	
	@GetMapping("/get-walk-in-customer-api-by-spOrAdmin")
	public ResponseEntity<ResponsesGetCustomerRegistrationDto> getWorkers(
	        @RequestParam(required = false) String userId,
	        @RequestParam(required = false) String userEmail,
	        @RequestParam(required = false) String userMobile,
	        @RequestParam(required = false) String userTown,
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size) {

	    Pageable pageable = PageRequest.of(page, size);
	    Page<CustomerRegistrationRespForSPAdmin> customerPage = service.getCustomerRegistration(userId, userEmail, userMobile, userTown, pageable);

	    ResponsesGetCustomerRegistrationDto response = new ResponsesGetCustomerRegistrationDto();
	    response.setMessage("Customer Details retrieved successfully.");
	    response.setStatus(true);
	    response.setCustomerData(customerPage.getContent());
	    // Set pagination fields
	    response.setCurrentPage(customerPage.getNumber());
	    response.setPageSize(customerPage.getSize());
	    response.setTotalElements(customerPage.getTotalElements());
	    response.setTotalPages(customerPage.getTotalPages());

	    return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
