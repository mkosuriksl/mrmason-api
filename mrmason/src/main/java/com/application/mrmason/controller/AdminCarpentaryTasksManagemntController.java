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

import com.application.mrmason.dto.AdminCarpentaryTasksManagemntRequestDTO;
import com.application.mrmason.dto.GenericResponse;
import com.application.mrmason.dto.ResponseGetAdminCarpentaryTasksManagemntManagementDto;
import com.application.mrmason.entity.AdminCarpentaryTasksManagemnt;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.service.AdminCarpentaryTasksManagemntService;

@RestController
@RequestMapping("/admintasks")
public class AdminCarpentaryTasksManagemntController {

	@Autowired
	private AdminCarpentaryTasksManagemntService service;

	@PostMapping("/add-carpentary-tasks")
	public ResponseEntity<GenericResponse<List<AdminCarpentaryTasksManagemnt>>> addTasks(
			@RequestBody AdminCarpentaryTasksManagemntRequestDTO requestDTO) {

		List<AdminCarpentaryTasksManagemnt> savedTasks = service.createAdmin(requestDTO);

		GenericResponse<List<AdminCarpentaryTasksManagemnt>> response = new GenericResponse<>(
				"Admin Carpentary Task Saved Successfully", true, savedTasks);

		return ResponseEntity.ok(response);
	}

	@PutMapping("/update-carpentary-tasks")
	public ResponseEntity<GenericResponse<List<AdminCarpentaryTasksManagemnt>>> updateTasks(
			@RequestBody List<AdminCarpentaryTasksManagemnt> requestList) {

		List<AdminCarpentaryTasksManagemnt> savedTasks = service.updateAdmin(requestList);

		GenericResponse<List<AdminCarpentaryTasksManagemnt>> response = new GenericResponse<>(
				"Admin Carpentary Task  Updated successfully", true, savedTasks);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/get-carpentary-tasks")
	public ResponseEntity<ResponseGetAdminCarpentaryTasksManagemntManagementDto> getTask(
			@RequestParam(required = false) String serviceCategory, @RequestParam(required = false) String taskName,
			@RequestParam(required = false) String taskId, @RequestParam(required = false) String adminTaskId,
			@RequestParam(required = false) RegSource regSource, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) throws AccessDeniedException {

		Pageable pageable = PageRequest.of(page, size);
		Page<AdminCarpentaryTasksManagemnt> srpqPage = service.getAdmin(serviceCategory, taskName, taskId, adminTaskId,
				regSource, pageable);
		ResponseGetAdminCarpentaryTasksManagemntManagementDto response = new ResponseGetAdminCarpentaryTasksManagemntManagementDto();

		response.setMessage("Admin Carpentary Task  details retrieved successfully.");
		response.setStatus(true);
		response.setServiceRequestCarpentaryQuotation(srpqPage.getContent());

		// Set pagination fields
		response.setCurrentPage(srpqPage.getNumber());
		response.setPageSize(srpqPage.getSize());
		response.setTotalElements(srpqPage.getTotalElements());
		response.setTotalPages(srpqPage.getTotalPages());

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
