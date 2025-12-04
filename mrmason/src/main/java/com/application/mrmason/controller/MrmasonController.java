package com.application.mrmason.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.application.mrmason.dto.PaginatedResponse;
import com.application.mrmason.dto.ServiceRequestHeaderDTO;
import com.application.mrmason.enums.Status;
import com.application.mrmason.service.MrmasonService;

@RestController
@RequestMapping("/mrmason-api")
public class MrmasonController {
	@Autowired
	private MrmasonService mrmasonService;

	@GetMapping
	public ResponseEntity<PaginatedResponse<ServiceRequestHeaderDTO>> getWithoutSecurity(
			@RequestParam(required = false) String fromRequestDate,
			@RequestParam(required = false) String toRequestDate, @RequestParam(required = false) String requestId,
			@RequestParam(required = false) String servicePersonId, @RequestParam(required = false) Status status,
			@RequestParam(required = false) String contactNumber, @RequestParam(required = false) String brand,
			@RequestParam(required = false) String vehicleId, @RequestParam(required = false) String model,
			@RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "10") int pageSize,
			@RequestParam(required = false) Map<String, String> requestParams) {

		PaginatedResponse<ServiceRequestHeaderDTO> response = mrmasonService.searchServiceRequests(fromRequestDate,
				toRequestDate, requestId, servicePersonId, status, contactNumber, brand, vehicleId, model, pageNo,
				pageSize, requestParams);

		return ResponseEntity.ok(response);
	}

}
