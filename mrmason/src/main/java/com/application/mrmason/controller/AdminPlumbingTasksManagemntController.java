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

import com.application.mrmason.dto.AdminPlumbingTasksManagemntRequestDTO;
import com.application.mrmason.dto.GenericResponse;
import com.application.mrmason.dto.ResponseGetAdminPlumbingTasksManagemntManagementDto;
import com.application.mrmason.entity.AdminPlumbingTasksManagemnt;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.service.AdminPlumbingTasksManagemntService;

@RestController
@RequestMapping("/admintasks")
public class AdminPlumbingTasksManagemntController {

	@Autowired
	private AdminPlumbingTasksManagemntService service;

	@PostMapping("/add-plumbing-tasks")
	public ResponseEntity<GenericResponse<List<AdminPlumbingTasksManagemnt>>> addTasks(
			@RequestBody AdminPlumbingTasksManagemntRequestDTO requestDTO) {

		List<AdminPlumbingTasksManagemnt> savedTasks = service.createAdmin(requestDTO);

		GenericResponse<List<AdminPlumbingTasksManagemnt>> response = new GenericResponse<>(
				"Admin Plumbing Task Saved Successfully", true, savedTasks);

		return ResponseEntity.ok(response);
	}

	@PutMapping("/update-plumbing-tasks")
	public ResponseEntity<GenericResponse<List<AdminPlumbingTasksManagemnt>>> updateTasks(
			@RequestBody List<AdminPlumbingTasksManagemnt> requestList) {

		List<AdminPlumbingTasksManagemnt> savedTasks = service.updateAdmin(requestList);

		GenericResponse<List<AdminPlumbingTasksManagemnt>> response = new GenericResponse<>(
				"Admin Plumbing Task  Updated successfully", true, savedTasks);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/get-plumbing-tasks")
	public ResponseEntity<ResponseGetAdminPlumbingTasksManagemntManagementDto> getTask(
			@RequestParam(required = false) String serviceCategory, @RequestParam(required = false) String taskName,
			@RequestParam(required = false) String taskId, @RequestParam(required = false) String adminTaskId,
			@RequestParam(required = false) RegSource regSource, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) throws AccessDeniedException {

		Pageable pageable = PageRequest.of(page, size);
		Page<AdminPlumbingTasksManagemnt> srpqPage = service.getAdmin(serviceCategory, taskName, taskId, adminTaskId,
				regSource, pageable);
		ResponseGetAdminPlumbingTasksManagemntManagementDto response = new ResponseGetAdminPlumbingTasksManagemntManagementDto();

		response.setMessage("Admin Plumbing Task  details retrieved successfully.");
		response.setStatus(true);
		response.setAdminPlumbingTasksManagemnt(srpqPage.getContent());

		// Set pagination fields
		response.setCurrentPage(srpqPage.getNumber());
		response.setPageSize(srpqPage.getSize());
		response.setTotalElements(srpqPage.getTotalElements());
		response.setTotalPages(srpqPage.getTotalPages());

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
