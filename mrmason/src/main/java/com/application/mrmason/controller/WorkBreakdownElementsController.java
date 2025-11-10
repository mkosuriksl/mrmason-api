package com.application.mrmason.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.application.mrmason.dto.GenericResponse;
import com.application.mrmason.dto.ResponseGetWorkBreakdownElementsDto;
import com.application.mrmason.entity.WorkBreakdownElements;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.service.WorkBreakdownElementsService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/workbreakdownElements")
@Validated
public class WorkBreakdownElementsController {

	@Autowired
	private WorkBreakdownElementsService service;

	@PostMapping("/add")
	public ResponseEntity<GenericResponse<WorkBreakdownElements>> addWorkBreakdownElements(
			@Valid @RequestBody WorkBreakdownElements request,@RequestParam RegSource regSource ) {

		WorkBreakdownElements saved = service.addWorkBreakdownElements(request,regSource);

		GenericResponse<WorkBreakdownElements> response = new GenericResponse<>(
				"Work break down elements added successfully", true, saved);
		return ResponseEntity.ok(response);
	}

	@PutMapping("/update")
	public ResponseEntity<GenericResponse<WorkBreakdownElements>> updateWorkBreakdownElements(
			@Valid @RequestBody WorkBreakdownElements request,
			@RequestParam RegSource regSource ) {

		WorkBreakdownElements updated = service.updateWorkBreakdownElements(request,regSource);
		return ResponseEntity.ok(new GenericResponse<>("Work break down elements updated successfully", true, updated));
	}

	@GetMapping("/get")
	public ResponseEntity<ResponseGetWorkBreakdownElementsDto> getHeaderWorkOrder(
			@RequestParam(required = false) String woOrderNo,
			@RequestParam(required = false) String taskId, @RequestParam(required = false) String subTaskId,
			@RequestParam(required = false) String actualStartDate, @RequestParam(required = false) String actualEndDate,
			@RequestParam(required = false) String tentativeStartdate, @RequestParam(required = false) String tentaiveEnddate,
			Pageable pageable) {

		Page<WorkBreakdownElements> pageResult = service.get(woOrderNo, taskId, subTaskId, actualStartDate,
				actualEndDate, tentativeStartdate, tentaiveEnddate, pageable);

		ResponseGetWorkBreakdownElementsDto response = new ResponseGetWorkBreakdownElementsDto();
		response.setMessage("get Work break down elements Retrieved Successfully");
		response.setStatus(true);
		response.setWorkBreakdownElements(pageResult.getContent());
		response.setCurrentPage(pageResult.getNumber());
		response.setPageSize(pageResult.getSize());
		response.setTotalElements(pageResult.getTotalElements());
		response.setTotalPages(pageResult.getTotalPages());

		return ResponseEntity.ok(response);
	}

}
