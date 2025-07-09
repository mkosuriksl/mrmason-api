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
import com.application.mrmason.dto.SPPopTaskRequestDTO;
import com.application.mrmason.dto.TaskResponseDto;
import com.application.mrmason.entity.SPPopTasksManagemnt;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.service.SPPopTasksManagemntService;

@RestController
@RequestMapping("/sptasks")
public class SPPopTasksManagemntController {

    @Autowired
    private SPPopTasksManagemntService service;

    @PostMapping("/add-pop-tasks")
    public ResponseEntity<GenericResponse<List<SPPopTasksManagemnt>>> addTasks(
            @RequestParam RegSource regSource, @RequestBody SPPopTaskRequestDTO requestDTO) throws AccessDeniedException {
        List<SPPopTasksManagemnt> savedTasks = service.createAdmin(regSource, requestDTO);
        GenericResponse<List<SPPopTasksManagemnt>> response = new GenericResponse<>("SP POP Task Saved Successfully", true, savedTasks);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update-pop-tasks")
    public ResponseEntity<GenericResponse<List<SPPopTasksManagemnt>>> updateTasks(
            @RequestParam RegSource regSource, @RequestBody List<SPPopTasksManagemnt> requestList) throws AccessDeniedException {
        List<SPPopTasksManagemnt> savedTasks = service.updateAdmin(regSource, requestList);
        GenericResponse<List<SPPopTasksManagemnt>> response = new GenericResponse<>("SP POP Task Updated successfully", true, savedTasks);
        return ResponseEntity.ok(response);
    }
    
//    @GetMapping("/get-pop-tasks-measureNames")
//	public ResponseEntity<List<TaskResponseDto>> getTaskDetails(
//	        @RequestParam(required = false) String serviceCategory,
//	        @RequestParam(required = false) String taskId,
//	        @RequestParam(required = false) String taskName,
//	        @RequestParam RegSource regSource)  throws AccessDeniedException  {
//
//	    List<TaskResponseDto> result = service.getTaskDetails(serviceCategory, taskId, taskName,regSource);
//	    return ResponseEntity.ok(result);
//	}
    
    @GetMapping("/get-pop-tasks-measureNames")
    public ResponseEntity<Map<String, Object>> getTaskDetails(
            @RequestParam(required = false) String serviceCategory,
            @RequestParam(required = false) String taskId,
            @RequestParam(required = false) String taskName,
            @RequestParam RegSource regSource,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) throws AccessDeniedException {

        Page<TaskResponseDto> paginatedResult = service.getTaskDetails(serviceCategory, taskId, taskName, regSource, page, size);

        Map<String, Object> response = new HashMap<>();
        response.put("data", paginatedResult.getContent());
        response.put("currentPage", paginatedResult.getNumber());
        response.put("totalItems", paginatedResult.getTotalElements());
        response.put("totalPages", paginatedResult.getTotalPages());

        return ResponseEntity.ok(response);
    }


}
