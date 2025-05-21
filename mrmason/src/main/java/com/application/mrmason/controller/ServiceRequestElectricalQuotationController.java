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
import com.application.mrmason.dto.ResponseGetServiceRequestElectricalQuotationDto;
import com.application.mrmason.dto.ServiceRequestElectricalQuotationWrapper;
import com.application.mrmason.entity.ServiceRequestElectricalQuotation;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.service.ServiceRequestElectricalQuotationService;

@RestController
public class ServiceRequestElectricalQuotationController {

	@Autowired
	private ServiceRequestElectricalQuotationService electricalQuotationService;

	@PostMapping("/add-serviceRequestElectricalquotation")
	public ResponseEntity<GenericResponse<List<ServiceRequestElectricalQuotation>>> createServiceRequestElectricalQuotationService(
			@RequestBody ServiceRequestElectricalQuotationWrapper requestWrapper, @RequestParam RegSource regSource) {

		List<ServiceRequestElectricalQuotation> savedAssignments = electricalQuotationService
				.createServiceRequestElectricalQuotationService(requestWrapper.getRequestId(), requestWrapper.getItems(),regSource);

		GenericResponse<List<ServiceRequestElectricalQuotation>> response = new GenericResponse<>(
				"Service Request Electrical Quotation created successfully", true, savedAssignments);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/get-serviceRequestElectricalquotation")
	public ResponseEntity<ResponseGetServiceRequestElectricalQuotationDto> getServiceRequestPaintQuotationService(
			@RequestParam(required = false) String requestLineId,
			@RequestParam(required = false) String requestLineIdDescription,
			@RequestParam(required = false) String requestId, @RequestParam(required = false) Integer qty,
			@RequestParam(required = false) Integer amount,
			@RequestParam(required = false) String status, @RequestParam(required = false) String spId,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

		Pageable pageable = PageRequest.of(page, size);
		Page<ServiceRequestElectricalQuotation> srpqPage = electricalQuotationService
				.getServiceRequestElectricalQuotationService(requestLineId, requestLineIdDescription,
						requestId, qty,amount, status, spId, pageable);
		ResponseGetServiceRequestElectricalQuotationDto response = new ResponseGetServiceRequestElectricalQuotationDto();

		response.setMessage("Service Request Electrical Quotation details retrieved successfully.");
		response.setStatus(true);
		response.setServiceRequestElectricalQuotation(srpqPage.getContent());

		// Set pagination fields
		response.setCurrentPage(srpqPage.getNumber());
		response.setPageSize(srpqPage.getSize());
		response.setTotalElements(srpqPage.getTotalElements());
		response.setTotalPages(srpqPage.getTotalPages());

		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@PutMapping("/update-serviceRequestElectricalquotation")
	public ResponseEntity<GenericResponse<List<ServiceRequestElectricalQuotation>>> updateWorkAssignment(
	        @RequestBody ServiceRequestElectricalQuotationWrapper requestWrapper,
	        @RequestParam RegSource regSource) {

	    List<ServiceRequestElectricalQuotation> updatedAssignments = electricalQuotationService
	            .updateServiceRequestElectricalQuotation(requestWrapper.getRequestId(), requestWrapper.getItems(),
	                    regSource);

	    GenericResponse<List<ServiceRequestElectricalQuotation>> response = new GenericResponse<>(
	            "Service Request Electrical Quotation updated successfully", true, updatedAssignments);

	    return ResponseEntity.ok(response);
	}

}
