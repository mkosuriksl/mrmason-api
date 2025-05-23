package com.application.mrmason.controller;

import java.nio.file.AccessDeniedException;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.application.mrmason.dto.AdminConstructionTasksManagementRequestDTO;
import com.application.mrmason.dto.GenericResponse;
import com.application.mrmason.dto.ResponseGetAdminConstructionTasksManagementDto;
import com.application.mrmason.entity.AdminConstructionTasksManagement;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.service.AdminConstructionTasksManagementService;

@RestController
@RequestMapping("/admintasks")
public class AdminConstructionTasksManagementController {

	@Autowired
	private AdminConstructionTasksManagementService service;

	@PostMapping("/add-construction-tasks")
	public ResponseEntity<GenericResponse<List<AdminConstructionTasksManagement>>> addTasks(
			@RequestBody AdminConstructionTasksManagementRequestDTO requestDTO) {

		List<AdminConstructionTasksManagement> savedTasks = service.createAdmin(requestDTO);

		GenericResponse<List<AdminConstructionTasksManagement>> response = new GenericResponse<>(
				"Admin Construction Task Saved Successfully", true, savedTasks);

		return ResponseEntity.ok(response);
	}

	@PutMapping("/update-construction-tasks")
	public ResponseEntity<GenericResponse<List<AdminConstructionTasksManagement>>> updateTasks(
			@RequestBody List<AdminConstructionTasksManagement> requestList) {

		List<AdminConstructionTasksManagement> savedTasks = service.updateAdmin(requestList);

		GenericResponse<List<AdminConstructionTasksManagement>> response = new GenericResponse<>(
				"Admin Construction Task  Updated successfully", true, savedTasks);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/get-construction-tasks")
	public ResponseEntity<ResponseGetAdminConstructionTasksManagementDto> getTask(
			@RequestParam(required = false) String serviceCategory, @RequestParam(required = false) String taskName,
			@RequestParam(required = false) String taskId, @RequestParam(required = false) String adminTaskId,
			@RequestParam(required = false) RegSource regSource, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) throws AccessDeniedException {

		Pageable pageable = PageRequest.of(page, size);
		Page<AdminConstructionTasksManagement> srpqPage = service.getAdmin(serviceCategory, taskName, taskId,
				adminTaskId, regSource, pageable);
		ResponseGetAdminConstructionTasksManagementDto response = new ResponseGetAdminConstructionTasksManagementDto();

		response.setMessage("Admin Construction Task  details retrieved successfully.");
		response.setStatus(true);
		response.setAdminConstructionTasksManagement(srpqPage.getContent());

		// Set pagination fields
		response.setCurrentPage(srpqPage.getNumber());
		response.setPageSize(srpqPage.getSize());
		response.setTotalElements(srpqPage.getTotalElements());
		response.setTotalPages(srpqPage.getTotalPages());

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
