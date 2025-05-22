package com.application.mrmason.controller;

import java.util.List;

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

import com.application.mrmason.dto.GenericResponse;
import com.application.mrmason.dto.ResponseGetServiceRequestCarpentaryQuotationDto;
import com.application.mrmason.dto.ServiceRequestCarpentaryQuotationWrapper;
import com.application.mrmason.entity.ServiceRequestCarpentaryQuotation;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.service.ServiceRequestCarpentaryQuotationService;

@RestController
public class ServiceRequestCarpentaryQuotationController {

	@Autowired
	private ServiceRequestCarpentaryQuotationService serviceRequestCarpentaryQuotationService;

	@PostMapping("/add-serviceRequestCarpentaryquotation")
	public ResponseEntity<GenericResponse<List<ServiceRequestCarpentaryQuotation>>> createServiceRequestCarpentaryQuotation(
			@RequestBody ServiceRequestCarpentaryQuotationWrapper requestWrapper, @RequestParam RegSource regSource) {

		List<ServiceRequestCarpentaryQuotation> savedAssignments = serviceRequestCarpentaryQuotationService
				.createServiceRequestCarpentaryQuotationService(requestWrapper.getRequestId(), requestWrapper.getItems(),
						regSource);

		GenericResponse<List<ServiceRequestCarpentaryQuotation>> response = new GenericResponse<>(
				"Service Request Carpentary Quotation created successfully", true, savedAssignments);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/get-serviceRequestCarpentaryquotation")
	public ResponseEntity<ResponseGetServiceRequestCarpentaryQuotationDto> getServiceRequestCarpentaryQuotation(
			@RequestParam(required = false) String requestLineId,
			@RequestParam(required = false) String requestLineIdDescription,
			@RequestParam(required = false) String requestId, @RequestParam(required = false) Integer quotationAmount,
			@RequestParam(required = false) String status, @RequestParam(required = false) String spId,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

		Pageable pageable = PageRequest.of(page, size);
		Page<ServiceRequestCarpentaryQuotation> srpqPage = serviceRequestCarpentaryQuotationService
				.getServiceRequestCarpentaryQuotationService( requestLineId, requestLineIdDescription,
						requestId, quotationAmount, status, spId, pageable);
		ResponseGetServiceRequestCarpentaryQuotationDto response = new ResponseGetServiceRequestCarpentaryQuotationDto();

		response.setMessage("Service Request Carpentary Quotation details retrieved successfully.");
		response.setStatus(true);
		response.setServiceRequestCarpentaryQuotation(srpqPage.getContent());

		// Set pagination fields
		response.setCurrentPage(srpqPage.getNumber());
		response.setPageSize(srpqPage.getSize());
		response.setTotalElements(srpqPage.getTotalElements());
		response.setTotalPages(srpqPage.getTotalPages());

		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@PutMapping("/update-serviceRequestCarpentaryquotation")
	public ResponseEntity<GenericResponse<List<ServiceRequestCarpentaryQuotation>>> updateServiceRequestCarpentaryQuotation(
	        @RequestBody ServiceRequestCarpentaryQuotationWrapper requestWrapper,
	        @RequestParam RegSource regSource) {

	    List<ServiceRequestCarpentaryQuotation> updatedAssignments = serviceRequestCarpentaryQuotationService
	            .updateServiceRequestCarpentaryQuotationService(requestWrapper.getRequestId(), requestWrapper.getItems(),
	                    regSource);

	    GenericResponse<List<ServiceRequestCarpentaryQuotation>> response = new GenericResponse<>(
	            "Service Request Carpentary Quotation updated successfully", true, updatedAssignments);

	    return ResponseEntity.ok(response);
	}


}
