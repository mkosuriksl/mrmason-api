package com.application.mrmason.controller;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
import com.application.mrmason.dto.TaskResponseDto;
import com.application.mrmason.entity.SPPaintTasksManagemnt;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.service.SPPaintTasksManagemntService;

@RestController
@RequestMapping("/sptasks")
public class SPPaintTasksManagemntController {

	@Autowired
	private SPPaintTasksManagemntService  service;

	@PostMapping("/add-paint-tasks")
	public ResponseEntity<GenericResponse<List<SPPaintTasksManagemnt>>> addTasks(
			@RequestParam RegSource regSource,@RequestBody SPPaintTaskRequestDTO requestDTO) throws AccessDeniedException{

		List<SPPaintTasksManagemnt> savedTasks = service.createAdmin(regSource,requestDTO);

		GenericResponse<List<SPPaintTasksManagemnt>> response = new GenericResponse<>("SP Paint Task Saved Successfully",
				true, savedTasks);

		return ResponseEntity.ok(response);
	}

	@PutMapping("/update-paint-tasks")
	public ResponseEntity<GenericResponse<List<SPPaintTasksManagemnt>>> addTasks(
			@RequestParam RegSource regSource,@RequestBody List<SPPaintTasksManagemnt> requestList) throws AccessDeniedException {

		List<SPPaintTasksManagemnt> savedTasks = service.updateAdmin(regSource,requestList);

		GenericResponse<List<SPPaintTasksManagemnt>> response = new GenericResponse<>("SP Paint Task  Updated successfully",
				true, savedTasks);

		return ResponseEntity.ok(response);
	}
	
//	@GetMapping("/get-paint-tasks-measureNames")
//	public ResponseEntity<List<TaskResponseDto>> getTaskDetails(
//	        @RequestParam(required = false) String serviceCategory,
//	        @RequestParam(required = false) String taskId,
//	        @RequestParam(required = false) String taskName,
//	        @RequestParam RegSource regSource)  throws AccessDeniedException  {
//
//	    List<TaskResponseDto> result = service.getTaskDetails(serviceCategory, taskId, taskName,regSource);
//	    return ResponseEntity.ok(result);
//	}

	@GetMapping("/get-paint-tasks-measureNames")
	public ResponseEntity<Map<String, Object>> getTaskDetails(
	        @RequestParam(required = false) String serviceCategory,
	        @RequestParam(required = false) String taskId,
	        @RequestParam(required = false) String taskName,
	        @RequestParam RegSource regSource,
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size) throws AccessDeniedException {

	    Page<TaskResponseDto> paginatedTasks = service.getTaskDetails(serviceCategory, taskId, taskName, regSource, page, size);

	    Map<String, Object> response = new HashMap<>();
	    response.put("data", paginatedTasks.getContent());
	    response.put("currentPage", paginatedTasks.getNumber());
	    response.put("totalItems", paginatedTasks.getTotalElements());
	    response.put("totalPages", paginatedTasks.getTotalPages());

	    return ResponseEntity.ok(response);
	}

}
