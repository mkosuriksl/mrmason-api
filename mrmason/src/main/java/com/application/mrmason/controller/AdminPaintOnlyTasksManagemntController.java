package com.application.mrmason.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import com.application.mrmason.dto.AdminPaintOnlyTaskRequestDTO;
import com.application.mrmason.dto.GenericResponse;
import com.application.mrmason.dto.ResponseGetAdminPaintOnlyTasksManagemntDto;
import com.application.mrmason.entity.AdminPaintOnlyTasksManagemnt;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.service.AdminPaintOnlyTasksManagemntService;


@RestController
@RequestMapping("/admintasks")
public class AdminPaintOnlyTasksManagemntController {

	@Autowired
	private AdminPaintOnlyTasksManagemntService service;

	@PostMapping("/add-paint-tasks")
	public ResponseEntity<GenericResponse<List<AdminPaintOnlyTasksManagemnt>>> addTasks(
			@RequestBody AdminPaintOnlyTaskRequestDTO requestDTO) {

		List<AdminPaintOnlyTasksManagemnt> savedTasks = service.createAdmin(requestDTO);

		GenericResponse<List<AdminPaintOnlyTasksManagemnt>> response = new GenericResponse<>("Admin Paint Task Saved Successfully",
				true, savedTasks);

		return ResponseEntity.ok(response);
	}

	@PutMapping("/update-paint-tasks")
	public ResponseEntity<GenericResponse<List<AdminPaintOnlyTasksManagemnt>>> addTasks(
			@RequestBody List<AdminPaintOnlyTasksManagemnt> requestList) {

		List<AdminPaintOnlyTasksManagemnt> savedTasks = service.updateAdmin(requestList);

		GenericResponse<List<AdminPaintOnlyTasksManagemnt>> response = new GenericResponse<>("Admin Paint Task  Updated successfully",
				true, savedTasks);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/get-paint-tasks")
	public ResponseEntity<ResponseGetAdminPaintOnlyTasksManagemntDto> getServiceRequestPaintQuotationService(
			@RequestParam(required = false) String serviceCategory, @RequestParam(required = false) String taskName,
			@RequestParam(required = false) String taskId, @RequestParam(required = false) String adminTaskId,
			@RequestParam(required = false) RegSource regSource,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) throws AccessDeniedException {

		Pageable pageable = PageRequest.of(page, size);
		Page<AdminPaintOnlyTasksManagemnt> srpqPage = service.getServiceRequestPaintQuotationService(serviceCategory,
				taskName, taskId, adminTaskId,regSource, pageable);
		ResponseGetAdminPaintOnlyTasksManagemntDto response = new ResponseGetAdminPaintOnlyTasksManagemntDto();

		response.setMessage("Admin Paint Task  details retrieved successfully.");
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
