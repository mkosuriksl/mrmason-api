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
import com.application.mrmason.dto.ResponseGetServiceRequestPaintQuotationDto;
import com.application.mrmason.dto.ServiceRequestPaintQuotationWrapper;
import com.application.mrmason.entity.ServiceRequestPaintQuotation;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.service.ServiceRequestPaintQuotationService;

@RestController
public class ServiceRequestPaintQuotationController {

	@Autowired
	private ServiceRequestPaintQuotationService serviceRequestPaintQuotationService;

	@PostMapping("/add-serviceRequestPaintquotation")
	public ResponseEntity<GenericResponse<List<ServiceRequestPaintQuotation>>> createWorkAssignment(
			@RequestBody ServiceRequestPaintQuotationWrapper requestWrapper, @RequestParam RegSource regSource) {

		List<ServiceRequestPaintQuotation> savedAssignments = serviceRequestPaintQuotationService
				.createServiceRequestPaintQuotationService(requestWrapper.getRequestId(), requestWrapper.getItems(),
						regSource);

		GenericResponse<List<ServiceRequestPaintQuotation>> response = new GenericResponse<>(
				"Service Request Paint Quotation created successfully", true, savedAssignments);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/get-serviceRequestPaintquotation")
	public ResponseEntity<ResponseGetServiceRequestPaintQuotationDto> getServiceRequestPaintQuotationService(
			@RequestParam(required = false) String requestLineId,
			@RequestParam(required = false) String requestLineIdDescription,
			@RequestParam(required = false) String requestId, @RequestParam(required = false) Integer quotationAmount,
			@RequestParam(required = false) String status, @RequestParam(required = false) String spId,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

		Pageable pageable = PageRequest.of(page, size);
		Page<ServiceRequestPaintQuotation> srpqPage = serviceRequestPaintQuotationService
				.getServiceRequestPaintQuotationService( requestLineId, requestLineIdDescription,
						requestId, quotationAmount, status, spId, pageable);
		ResponseGetServiceRequestPaintQuotationDto response = new ResponseGetServiceRequestPaintQuotationDto();

		response.setMessage("Service Request Paint Quotation details retrieved successfully.");
		response.setStatus(true);
		response.setServiceRequestPaintQuotation(srpqPage.getContent());

		// Set pagination fields
		response.setCurrentPage(srpqPage.getNumber());
		response.setPageSize(srpqPage.getSize());
		response.setTotalElements(srpqPage.getTotalElements());
		response.setTotalPages(srpqPage.getTotalPages());

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
