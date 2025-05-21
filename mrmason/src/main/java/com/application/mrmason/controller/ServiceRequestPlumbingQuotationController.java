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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.application.mrmason.dto.GenericResponse;
import com.application.mrmason.dto.ResponseGetServiceRequestPlumbingQuotationDto;
import com.application.mrmason.entity.ServiceRequestPlumbingQuotation;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.service.ServiceRequestPlumbingQuotationService;

@RestController
public class ServiceRequestPlumbingQuotationController {

	@Autowired
	private ServiceRequestPlumbingQuotationService plumbingQuotationService;

	@PostMapping("/add-serviceRequestPlumbingquotation")
	public ResponseEntity<GenericResponse<List<ServiceRequestPlumbingQuotation>>> createWorkAssignment(
			@RequestBody List<ServiceRequestPlumbingQuotation> request, @RequestParam RegSource regSource) {

		List<ServiceRequestPlumbingQuotation> savedAssignments = plumbingQuotationService
				.createServiceRequestPlumbingQuotation(request, regSource);

		GenericResponse<List<ServiceRequestPlumbingQuotation>> response = new GenericResponse<>(
				"Service Request Plumbing Quotation created successfully", true, savedAssignments);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/get-serviceRequestPlumbingquotation")
	public ResponseEntity<ResponseGetServiceRequestPlumbingQuotationDto> getServiceRequestPaintQuotationService(
			@RequestParam(required = false) String serviceRequestPlumbingId,
			@RequestParam(required = false) String requestLineId,
			@RequestParam(required = false) String requestLineIdDescription,
			@RequestParam(required = false) String requestId, @RequestParam(required = false) Integer quotationAmount,
			@RequestParam(required = false) String status, @RequestParam(required = false) String spId,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

		Pageable pageable = PageRequest.of(page, size);
		Page<ServiceRequestPlumbingQuotation> srpqPage = plumbingQuotationService
				.getServiceRequestPlumbingQuotation(serviceRequestPlumbingId, requestLineId, requestLineIdDescription,
						requestId, quotationAmount, status, spId, pageable);
		ResponseGetServiceRequestPlumbingQuotationDto response = new ResponseGetServiceRequestPlumbingQuotationDto();

		response.setMessage("Service Request Plumbing Quotation details retrieved successfully.");
		response.setStatus(true);
		response.setRequestPlumbingQuotations(srpqPage.getContent());

		// Set pagination fields
		response.setCurrentPage(srpqPage.getNumber());
		response.setPageSize(srpqPage.getSize());
		response.setTotalElements(srpqPage.getTotalElements());
		response.setTotalPages(srpqPage.getTotalPages());

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
