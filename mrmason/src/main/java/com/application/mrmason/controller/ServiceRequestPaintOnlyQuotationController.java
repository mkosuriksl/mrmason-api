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
import com.application.mrmason.dto.ResponseGetServiceRequestPaintOnlyQuotationDto;
import com.application.mrmason.dto.ResponseGetServiceRequestPlumbingQuotationDto;
import com.application.mrmason.dto.ServiceRequestPaintOnlyQuotationWrapper;
import com.application.mrmason.dto.ServiceRequestPlumbingQuotationWrapper;
import com.application.mrmason.entity.ServiceRequestPaintOnlyQuotation;
import com.application.mrmason.entity.ServiceRequestPlumbingQuotation;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.service.ServiceRequestPaintOnlyQuotationService;
import com.application.mrmason.service.ServiceRequestPlumbingQuotationService;

@RestController
public class ServiceRequestPaintOnlyQuotationController {

	@Autowired
	private ServiceRequestPaintOnlyQuotationService service;

	@PostMapping("/add-serviceRequestPaintquotation")
	public ResponseEntity<GenericResponse<List<ServiceRequestPaintOnlyQuotation>>> createWorkAssignment(
			@RequestBody ServiceRequestPaintOnlyQuotationWrapper requestWrapper, @RequestParam RegSource regSource) {

		List<ServiceRequestPaintOnlyQuotation> savedAssignments = service
				.createServiceRequestPaintOnlyQuotation(requestWrapper.getRequestId(), requestWrapper.getItems(), regSource);

		GenericResponse<List<ServiceRequestPaintOnlyQuotation>> response = new GenericResponse<>(
				"Service Request Paint Quotation created successfully", true, savedAssignments);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/get-serviceRequestPaintquotation")
	public ResponseEntity<ResponseGetServiceRequestPaintOnlyQuotationDto> getServiceRequestPaintQuotationService(
			@RequestParam(required = false) String requestLineId,
			@RequestParam(required = false) String requestLineIdDescription,
			@RequestParam(required = false) String requestId, @RequestParam(required = false) Integer quotationAmount,
			@RequestParam(required = false) String status, @RequestParam(required = false) String spId,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

		Pageable pageable = PageRequest.of(page, size);
		Page<ServiceRequestPaintOnlyQuotation> srpqPage = service
				.getServiceRequestPaintOnlyQuotation(requestLineId, requestLineIdDescription,
						requestId, quotationAmount, status, spId, pageable);
		ResponseGetServiceRequestPaintOnlyQuotationDto response = new ResponseGetServiceRequestPaintOnlyQuotationDto();

		response.setMessage("Service Request Paint Quotation details retrieved successfully.");
		response.setStatus(true);
		response.setRequestPaintOnlyQuotations(srpqPage.getContent());

		// Set pagination fields
		response.setCurrentPage(srpqPage.getNumber());
		response.setPageSize(srpqPage.getSize());
		response.setTotalElements(srpqPage.getTotalElements());
		response.setTotalPages(srpqPage.getTotalPages());

		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@PutMapping("/update-serviceRequestPaintquotation")
	public ResponseEntity<GenericResponse<List<ServiceRequestPaintOnlyQuotation>>> updateWorkAssignment(
	        @RequestBody ServiceRequestPaintOnlyQuotationWrapper requestWrapper,
	        @RequestParam RegSource regSource) {

	    List<ServiceRequestPaintOnlyQuotation> updatedAssignments = service
	            .updateServiceRequestPaintOnlyQuotation(requestWrapper.getRequestId(), requestWrapper.getItems(),
	                    regSource);

	    GenericResponse<List<ServiceRequestPaintOnlyQuotation>> response = new GenericResponse<>(
	            "Service Request Paint Quotation updated successfully", true, updatedAssignments);

	    return ResponseEntity.ok(response);
	}

}
