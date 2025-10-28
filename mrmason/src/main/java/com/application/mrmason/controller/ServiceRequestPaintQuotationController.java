package com.application.mrmason.controller;

import java.util.List;
import java.util.Map;

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
import com.application.mrmason.dto.ResponseGetServiceRequestPaintQuotationDto;
import com.application.mrmason.dto.ServiceRequestItem;
import com.application.mrmason.dto.ServiceRequestPaintQuotationWrapper;
import com.application.mrmason.entity.ServiceRequestPaintQuotation;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.service.ServiceRequestPaintQuotationService;

@RestController
public class ServiceRequestPaintQuotationController {

	@Autowired
	private ServiceRequestPaintQuotationService serviceRequestPaintQuotationService;

	@PostMapping("/add-serviceRequestAllquotation")
	public ResponseEntity<GenericResponse<List<ServiceRequestPaintQuotation>>> createWorkAssignment(
	        @RequestBody ServiceRequestPaintQuotationWrapper requestWrapper,
	        @RequestParam RegSource regSource) {

	    // Extract requestId and items from wrapper
	    String requestId = requestWrapper.getRequestId();
	    String serviceCategory=requestWrapper.getServiceCategory();
	    List<ServiceRequestItem> items = requestWrapper.getItems();

	    // Validate input
	    if (requestId == null || requestId.isEmpty() || items == null || items.isEmpty()) {
	        return ResponseEntity.badRequest().body(
	                new GenericResponse<>("requestId or items must not be empty", false, null));
	    }

	    // Call service method
	    List<ServiceRequestPaintQuotation> savedItems =
	            serviceRequestPaintQuotationService.createServiceRequestPaintQuotationService(requestId,serviceCategory, items, regSource);

	    // Build response
	    GenericResponse<List<ServiceRequestPaintQuotation>> response = new GenericResponse<>(
	            "Service Request BCEPP Quotation created successfully", true, savedItems);

	    return ResponseEntity.ok(response);
	}

	@GetMapping("/get-serviceRequestAllquotation")
	public ResponseEntity<Map<String, Object>> getAllGroupedQuotations(
            @RequestParam(required = false) String admintasklineId,
            @RequestParam(required = false) String taskDescription,
            @RequestParam(required = false) String serviceCategory,
            @RequestParam(required = false) String taskId,
            @RequestParam(required = false) String measureNames,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String spId,
            @RequestParam(required = false) String requestId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Map<String, Object> response = serviceRequestPaintQuotationService.getAllGroupedQuotations(
                admintasklineId, taskDescription, serviceCategory,
                taskId, measureNames, status, spId, requestId, page, size
        );

        return ResponseEntity.ok(response);
    }
//	public ResponseEntity<ResponseGetServiceRequestPaintQuotationDto> getServiceRequestPaintQuotationService(
//			@RequestParam(required = false) String admintasklineId,
//			@RequestParam(required = false) String taskDescription,@RequestParam(required = false) String serviceCategory,
//			@RequestParam(required = false) String taskId, @RequestParam(required = false) String measureNames,
//			@RequestParam(required = false) String status, @RequestParam(required = false) String spId,
//			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
//
//		Pageable pageable = PageRequest.of(page, size);
//		Page<ServiceRequestPaintQuotation> srpqPage = serviceRequestPaintQuotationService
//				.getServiceRequestPaintQuotationService( admintasklineId, taskDescription,
//						taskId, serviceCategory,measureNames, status, spId, pageable);
//		ResponseGetServiceRequestPaintQuotationDto response = new ResponseGetServiceRequestPaintQuotationDto();
//
//		response.setMessage("Service Request BCEPP Quotation details retrieved successfully.");
//		response.setStatus(true);
//		response.setServiceRequestPaintQuotation(srpqPage.getContent());
//
//		// Set pagination fields
//		response.setCurrentPage(srpqPage.getNumber());
//		response.setPageSize(srpqPage.getSize());
//		response.setTotalElements(srpqPage.getTotalElements());
//		response.setTotalPages(srpqPage.getTotalPages());
//
//		return new ResponseEntity<>(response, HttpStatus.OK);
//	}
	
	@PutMapping("/update-serviceRequestAllquotation")
	public ResponseEntity<GenericResponse<List<ServiceRequestPaintQuotation>>> updateServiceRequestQuotation(
	        @RequestParam String taskId,
	        @RequestBody List<ServiceRequestPaintQuotation> dtoList,
	        @RequestParam RegSource regSource) {

	    // Validate input
	    if (taskId == null || taskId.isEmpty()) {
	        return ResponseEntity.badRequest().body(
	                new GenericResponse<>("Task ID must not be empty", false, null));
	    }
	    if (dtoList == null || dtoList.isEmpty()) {
	        return ResponseEntity.badRequest().body(
	                new GenericResponse<>("Update list must not be empty", false, null));
	    }

	    try {
	        List<ServiceRequestPaintQuotation> updatedList =
	                serviceRequestPaintQuotationService.updateServiceRequestQuotation(taskId, dtoList, regSource);

	        return ResponseEntity.ok(new GenericResponse<>(
	                "Service Request Quotations updated successfully", true, updatedList));
	    } catch (IllegalArgumentException e) {
	        return ResponseEntity.badRequest().body(
	                new GenericResponse<>(e.getMessage(), false, null));
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
	                new GenericResponse<>("An unexpected error occurred: " + e.getMessage(), false, null));
	    }
	}


}
