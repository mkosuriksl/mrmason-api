package com.application.mrmason.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.application.mrmason.dto.GenericResponse;
import com.application.mrmason.dto.ResponseGetWorkOrderSRHdrAndCustomerDto;
import com.application.mrmason.dto.ResponseGetWorkOrderSRHeaderQuotationDto;
import com.application.mrmason.dto.WorkOrderRequest;
import com.application.mrmason.entity.ServiceRequestHeaderAllQuotation2;
import com.application.mrmason.entity.ServiceRequestPaintQuotation2;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.service.ServiceRequestPaintQuotationService2;

@RestController
public class WordeOrderServiceRequestQuotationController {

	@Autowired
	private ServiceRequestPaintQuotationService2 serviceRequestPaintQuotationService;

	@PostMapping("/add-workorder")
	public ResponseEntity<GenericResponse<Map<String, Object>>> duplicateQuotation(
			@RequestBody WorkOrderRequest workOrderRequest, @RequestParam RegSource regSource) {

		GenericResponse<Map<String, Object>> response = serviceRequestPaintQuotationService
				.duplicateQuotationToRepo2(workOrderRequest, regSource);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/get-workorder-header")
	public ResponseEntity<ResponseGetWorkOrderSRHeaderQuotationDto> getHeaderWorkOrder(
			@RequestParam(required = false) String workOrderId, @RequestParam(required = false) String quotationId,
			@RequestParam(required = false) String fromDate, @RequestParam(required = false) String toDate,
			@RequestParam(required = false) String spId, @RequestParam(required = false) String status,
			Pageable pageable, @RequestParam(required = false) Map<String, String> requestParams) {

		Page<ServiceRequestHeaderAllQuotation2> pageResult = serviceRequestPaintQuotationService
				.getHeaderWorkOrder(workOrderId, quotationId, fromDate, toDate, spId, status, pageable, requestParams);

		ResponseGetWorkOrderSRHeaderQuotationDto response = new ResponseGetWorkOrderSRHeaderQuotationDto();
		response.setMessage("Work Order Header Details Retrieved Successfully");
		response.setStatus(true);
		response.setWorkSRHeaderQuotation(pageResult.getContent());
		response.setCurrentPage(pageResult.getNumber());
		response.setPageSize(pageResult.getSize());
		response.setTotalElements(pageResult.getTotalElements());
		response.setTotalPages(pageResult.getTotalPages());

		return ResponseEntity.ok(response);
	}

	@GetMapping("/get-workorder-details")
	public ResponseEntity<GenericResponse<List<ServiceRequestPaintQuotation2>>> getWorkOrderDetails(
			@RequestParam(required = false) String workOrderLineId,
			@RequestParam(required = false) String taskDescription,
			@RequestParam(required = false) String serviceCategory, @RequestParam(required = false) String taskId,
			@RequestParam(required = false) String measureNames, @RequestParam(required = false) String status,
			@RequestParam(required = false) String spId, @RequestParam(required = false) String quotationId,
			@RequestParam(required = false) String workOrderId,
			@RequestParam(required = false) Map<String, String> requestParams) {

		List<ServiceRequestPaintQuotation2> workOrders = serviceRequestPaintQuotationService.getWorkOrderDetails(
				workOrderLineId, taskDescription, serviceCategory, taskId, measureNames, status, spId, quotationId,
				workOrderId, requestParams);

		GenericResponse<List<ServiceRequestPaintQuotation2>> response = new GenericResponse<>(
				"Fetched work order details successfully", true, workOrders);

		return ResponseEntity.ok(response);
	}

	@PutMapping("/update-workorder-quotation")
	public ResponseEntity<GenericResponse<List<ServiceRequestPaintQuotation2>>> updateWorkOrderQuotation(
			@RequestParam String workOrderId, @RequestBody List<ServiceRequestPaintQuotation2> quotationUpdates,
			@RequestParam RegSource regSource) {

		List<ServiceRequestPaintQuotation2> updatedList = serviceRequestPaintQuotationService
				.updateWorkOrderQuotation(workOrderId, quotationUpdates, regSource);

		GenericResponse<List<ServiceRequestPaintQuotation2>> response = new GenericResponse<>(
				"Work order and quotation details updated successfully", true, updatedList);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/get-workorderheaderAndCustomer")
	public ResponseEntity<ResponseGetWorkOrderSRHdrAndCustomerDto> getWorkOrderSRHdrAndCustomer(
			@RequestParam(required = false) String workOrderId, @RequestParam(required = false) String quotationId,
			@RequestParam(required = false) String fromQuotatedDate,
			@RequestParam(required = false) String toQuotatedDate, @RequestParam(required = false) String status,
			@RequestParam(required = false) String spId, @RequestParam(required = false) String userid,
			@RequestParam(required = false) String userEmail, @RequestParam(required = false) String userMobile,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
			@RequestParam(required = false) Map<String, String> requestParams) {

		Pageable pageable = PageRequest.of(page, size, Sort.by("updatedDate").descending());

		ResponseGetWorkOrderSRHdrAndCustomerDto response = serviceRequestPaintQuotationService
				.getWorkOrderWithCustomerDetails(workOrderId, quotationId, fromQuotatedDate, toQuotatedDate, status,
						spId, userid, userEmail, userMobile, pageable,requestParams);

		return ResponseEntity.ok(response);
	}
}
