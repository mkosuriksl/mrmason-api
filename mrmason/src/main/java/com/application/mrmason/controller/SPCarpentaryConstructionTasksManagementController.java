package com.application.mrmason.controller;

import java.nio.file.AccessDeniedException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.application.mrmason.dto.GenericResponse;
import com.application.mrmason.dto.SPCarpentaryConstructionTaskRequestDto;
import com.application.mrmason.dto.TaskResponseDto;
import com.application.mrmason.entity.SPCarpentaryConstructionTasksManagement;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.service.SPCarpentaryConstructionTasksManagementService;

@RestController
@RequestMapping("/sptasks")
public class SPCarpentaryConstructionTasksManagementController {

	@Autowired
	private SPCarpentaryConstructionTasksManagementService service;

	@PostMapping("/add-carpentary-tasks")
	public ResponseEntity<GenericResponse<List<SPCarpentaryConstructionTasksManagement>>> addTasks(
			@RequestParam RegSource regSource, @RequestBody SPCarpentaryConstructionTaskRequestDto requestDTO) throws AccessDeniedException {

		List<SPCarpentaryConstructionTasksManagement> savedTasks = service.createAdmin(regSource, requestDTO);

		GenericResponse<List<SPCarpentaryConstructionTasksManagement>> response = new GenericResponse<>("SP Carpentary Task Saved Successfully",
				true, savedTasks);

		return ResponseEntity.ok(response);
	}

	@PutMapping("/update-carpentary-tasks")
	public ResponseEntity<GenericResponse<List<SPCarpentaryConstructionTasksManagement>>> updateTasks(
			@RequestParam RegSource regSource, @RequestBody List<SPCarpentaryConstructionTasksManagement> requestList) throws AccessDeniedException {

		List<SPCarpentaryConstructionTasksManagement> savedTasks = service.updateAdmin(regSource, requestList);

		GenericResponse<List<SPCarpentaryConstructionTasksManagement>> response = new GenericResponse<>("SP Carpentary Task Updated successfully",
				true, savedTasks);

		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/get-carpentary-tasks-measureNames")
	public ResponseEntity<List<TaskResponseDto>> getTaskDetails(
	        @RequestParam(required = false) String serviceCategory,
	        @RequestParam(required = false) String taskId,
	        @RequestParam(required = false) String taskName)   {

	    List<TaskResponseDto> result = service.getTaskDetails(serviceCategory, taskId, taskName);
	    return ResponseEntity.ok(result);
	}
}
