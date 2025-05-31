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
import com.application.mrmason.dto.SPPaintTaskRequestDTO;
import com.application.mrmason.dto.SPPlumbingTaskRequestDTO;
import com.application.mrmason.dto.TaskResponseDto;
import com.application.mrmason.entity.SPPaintTasksManagemnt;
import com.application.mrmason.entity.SPPlumbingTasksManagemnt;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.service.SPPaintTasksManagemntService;
import com.application.mrmason.service.SPPlumbingTasksManagemntService;

@RestController
@RequestMapping("/sptasks")
public class SPPlumbingTasksManagemntController {

	@Autowired
	private SPPlumbingTasksManagemntService  service;

	@PostMapping("/add-plumbing-tasks")
	public ResponseEntity<GenericResponse<List<SPPlumbingTasksManagemnt>>> addTasks(
			@RequestParam RegSource regSource,@RequestBody SPPlumbingTaskRequestDTO requestDTO) throws AccessDeniedException{

		List<SPPlumbingTasksManagemnt> savedTasks = service.createAdmin(regSource,requestDTO);

		GenericResponse<List<SPPlumbingTasksManagemnt>> response = new GenericResponse<>("SP Plumbing Task Saved Successfully",
				true, savedTasks);

		return ResponseEntity.ok(response);
	}

	@PutMapping("/update-plumbing-tasks")
	public ResponseEntity<GenericResponse<List<SPPlumbingTasksManagemnt>>> addTasks(
			@RequestParam RegSource regSource,@RequestBody List<SPPlumbingTasksManagemnt> requestList) throws AccessDeniedException {

		List<SPPlumbingTasksManagemnt> savedTasks = service.updateAdmin(regSource,requestList);

		GenericResponse<List<SPPlumbingTasksManagemnt>> response = new GenericResponse<>("SP Plumbing Task  Updated successfully",
				true, savedTasks);

		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/get-plumbing-tasks-measureNames")
	public ResponseEntity<List<TaskResponseDto>> getTaskDetails(
	        @RequestParam(required = false) String serviceCategory,
	        @RequestParam(required = false) String taskId,
	        @RequestParam(required = false) String taskName)   {

	    List<TaskResponseDto> result = service.getTaskDetails(serviceCategory, taskId, taskName);
	    return ResponseEntity.ok(result);
	}

}
