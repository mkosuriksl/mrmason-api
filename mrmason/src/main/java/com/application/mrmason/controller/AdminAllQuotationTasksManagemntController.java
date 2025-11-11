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

import com.application.mrmason.dto.AdminPaintTaskRequestDTO;
import com.application.mrmason.dto.GenericResponse;
import com.application.mrmason.dto.ResponseGetAdminPaintTasksManagemntDto;
import com.application.mrmason.dto.TaskResponseDto;
import com.application.mrmason.entity.AdminPaintTasksManagemnt;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.service.AdminPaintTasksManagemntService;

@RestController
@RequestMapping("/admin-all-quotation-task")
public class AdminAllQuotationTasksManagemntController {

	@Autowired
	private AdminPaintTasksManagemntService service;

	@PostMapping("/add")
	public ResponseEntity<GenericResponse<List<AdminPaintTasksManagemnt>>> addTasks(
			@RequestBody AdminPaintTaskRequestDTO requestDTO) {

		List<AdminPaintTasksManagemnt> savedTasks = service.createAdmin(requestDTO);

		GenericResponse<List<AdminPaintTasksManagemnt>> response = new GenericResponse<>("Admin Paint Task Saved Successfully",
				true, savedTasks);

		return ResponseEntity.ok(response);
	}

	@PutMapping("/update")
	public ResponseEntity<GenericResponse<List<AdminPaintTasksManagemnt>>> addTasks(
			@RequestBody List<AdminPaintTasksManagemnt> requestList) {

		List<AdminPaintTasksManagemnt> savedTasks = service.updateAdmin(requestList);

		GenericResponse<List<AdminPaintTasksManagemnt>> response = new GenericResponse<>("Admin Paint Task  Updated successfully",
				true, savedTasks);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/get")
	public ResponseEntity<ResponseGetAdminPaintTasksManagemntDto> getServiceRequestPaintQuotationService(
			@RequestParam(required = false) String serviceCategory, @RequestParam(required = false) String taskName,
			@RequestParam(required = false) String taskId, @RequestParam(required = false) String adminTaskId,
			@RequestParam(required = false) RegSource regSource,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) throws AccessDeniedException {

		Pageable pageable = PageRequest.of(page, size);
		Page<AdminPaintTasksManagemnt> srpqPage = service.getServiceRequestPaintQuotationService(serviceCategory,
				taskName, taskId, adminTaskId,regSource, pageable);
		ResponseGetAdminPaintTasksManagemntDto response = new ResponseGetAdminPaintTasksManagemntDto();

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
	
	@GetMapping("/get-by-service-category")
	public ResponseEntity<List<TaskResponseDto>> getTaskDetails(
	        @RequestParam(required = false) String serviceCategory,
	        @RequestParam(required = false) String taskId,
	        @RequestParam(required = false) String taskName)   {

	    List<TaskResponseDto> result = service.getTaskDetails(serviceCategory, taskId, taskName);
	    return ResponseEntity.ok(result);
	}


}
