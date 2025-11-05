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
import com.application.mrmason.dto.ResponseGetServiceRequestBuildingConstructionQuotationDto;
import com.application.mrmason.dto.ServiceRequestBuildingConstructionQuotationWrapper;
import com.application.mrmason.dto.ServiceRequestBuildingConstructionQuotationWrapper2;
import com.application.mrmason.entity.ServiceRequestBuildingConstructionQuotation;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.service.ServiceRequestBuildingConstructionQuotationService;

@RestController
public class ServiceRequestBuildingConstructionQuotationController {

	@Autowired
	private ServiceRequestBuildingConstructionQuotationService buildingConstructionQuotationService;

	@PostMapping("/add-serviceRequestBuildingConstructionQuotation")
	public ResponseEntity<GenericResponse<List<ServiceRequestBuildingConstructionQuotation>>> createServiceRequestBuildingConstructionQuotation(
			@RequestBody ServiceRequestBuildingConstructionQuotationWrapper requestWrapper, @RequestParam RegSource regSource) {

		List<ServiceRequestBuildingConstructionQuotation> savedAssignments = buildingConstructionQuotationService
				.createServiceRequestBuildingConstructionQuotation(requestWrapper.getRequestId(), requestWrapper.getItems(),
						regSource);

		GenericResponse<List<ServiceRequestBuildingConstructionQuotation>> response = new GenericResponse<>(
				"Service Request Building Construction Quotation created successfully", true, savedAssignments);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/get-serviceRequestBuildingConstructionQuotation")
	public ResponseEntity<ResponseGetServiceRequestBuildingConstructionQuotationDto> getServiceRequestBuildingConstructionQuotation(
			@RequestParam(required = false) String requestLineId,
			@RequestParam(required = false) String requestLineIdDescription,
			@RequestParam(required = false) String requestId, @RequestParam(required = false) Integer quotationAmount,
			@RequestParam(required = false) String status, @RequestParam(required = false) String spId,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

		Pageable pageable = PageRequest.of(page, size);
		Page<ServiceRequestBuildingConstructionQuotation> srpqPage = buildingConstructionQuotationService
				.getServiceRequestBuildingConstructionQuotation( requestLineId, requestLineIdDescription,
						requestId, quotationAmount, status, spId, pageable);
		ResponseGetServiceRequestBuildingConstructionQuotationDto response = new ResponseGetServiceRequestBuildingConstructionQuotationDto();

		response.setMessage("Service Request Building Construction Quotation details retrieved successfully.");
		response.setStatus(true);
		response.setServiceRequestBuildingConstructionQuotation(srpqPage.getContent());

		// Set pagination fields
		response.setCurrentPage(srpqPage.getNumber());
		response.setPageSize(srpqPage.getSize());
		response.setTotalElements(srpqPage.getTotalElements());
		response.setTotalPages(srpqPage.getTotalPages());

		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@PutMapping("/update-serviceRequestBuildingConstructionQuotation")
	public ResponseEntity<GenericResponse<List<ServiceRequestBuildingConstructionQuotation>>> updateServiceRequestBuildingConstructionQuotation(
	        @RequestBody ServiceRequestBuildingConstructionQuotationWrapper2 requestWrapper,
	        @RequestParam RegSource regSource) {

	    List<ServiceRequestBuildingConstructionQuotation> updatedAssignments = buildingConstructionQuotationService
	            .updateServiceRequestBuildingConstructionQuotation(requestWrapper.getQuotationId(), requestWrapper.getItems(),
	                    regSource);

	    GenericResponse<List<ServiceRequestBuildingConstructionQuotation>> response = new GenericResponse<>(
	            "Service Request Building Construction Quotation updated successfully", true, updatedAssignments);

	    return ResponseEntity.ok(response);                  
	}


}
