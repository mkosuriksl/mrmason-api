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

import com.application.mrmason.dto.AdminElectricalTaskRequestDTO;
import com.application.mrmason.dto.GenericResponse;
import com.application.mrmason.dto.ResponseGetAdminElectricalTasksManagemntDto;
import com.application.mrmason.entity.AdminElectricalTasksManagement;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.service.AdminElectricalTasksManagemntService;

@RestController
@RequestMapping("/admintasks")
public class AdminElectricalManagemntController {

	@Autowired
	private AdminElectricalTasksManagemntService service;

	@PostMapping("/add-electrical-tasks")
	public ResponseEntity<GenericResponse<List<AdminElectricalTasksManagement>>> addTasks(
			@RequestBody AdminElectricalTaskRequestDTO requestDTO) {

		List<AdminElectricalTasksManagement> savedTasks = service.createAdmin(requestDTO);

		GenericResponse<List<AdminElectricalTasksManagement>> response = new GenericResponse<>(
				"Admin electrical Task Saved Successfully", true, savedTasks);

		return ResponseEntity.ok(response);
	}

	@PutMapping("/update-electrical-tasks")
	public ResponseEntity<GenericResponse<List<AdminElectricalTasksManagement>>> addTasks(
			@RequestBody List<AdminElectricalTasksManagement> requestList) {

		List<AdminElectricalTasksManagement> savedTasks = service.updateAdmin(requestList);

		GenericResponse<List<AdminElectricalTasksManagement>> response = new GenericResponse<>(
				"Admin electrical Task  Updated successfully", true, savedTasks);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/get-electrical-tasks")
	public ResponseEntity<ResponseGetAdminElectricalTasksManagemntDto> getServiceRequestPaintQuotationService(
			@RequestParam(required = false) String serviceCategory, @RequestParam(required = false) String taskName,
			@RequestParam(required = false) String taskId, @RequestParam(required = false) String adminTaskId,
			@RequestParam(required = false) RegSource regSource, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) throws AccessDeniedException {

		Pageable pageable = PageRequest.of(page, size);
		Page<AdminElectricalTasksManagement> srpqPage = service.getAdmin(serviceCategory, taskName, taskId, adminTaskId,
				regSource, pageable);
		ResponseGetAdminElectricalTasksManagemntDto response = new ResponseGetAdminElectricalTasksManagemntDto();

		response.setMessage("Admin electrical Task  details retrieved successfully.");
		response.setStatus(true);
		response.setServiceRequestElectricalQuotation(srpqPage.getContent());

		// Set pagination fields
		response.setCurrentPage(srpqPage.getNumber());
		response.setPageSize(srpqPage.getSize());
		response.setTotalElements(srpqPage.getTotalElements());
		response.setTotalPages(srpqPage.getTotalPages());

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
