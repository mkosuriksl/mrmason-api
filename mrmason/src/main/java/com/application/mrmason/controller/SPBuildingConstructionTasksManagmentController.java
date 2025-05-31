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
import com.application.mrmason.dto.SPBuildingConstructionTaskRequestDTO;
import com.application.mrmason.dto.TaskResponseDto;
import com.application.mrmason.entity.SPBuildingConstructionTasksManagment;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.service.SPBuildingConstructionTasksManagmentService;

@RestController
@RequestMapping("/sptasks")
public class SPBuildingConstructionTasksManagmentController {

    @Autowired
    private SPBuildingConstructionTasksManagmentService service;

    @PostMapping("/add-building-construction-tasks")
    public ResponseEntity<GenericResponse<List<SPBuildingConstructionTasksManagment>>> addTasks(
            @RequestParam RegSource regSource, @RequestBody SPBuildingConstructionTaskRequestDTO requestDTO) throws AccessDeniedException {
        List<SPBuildingConstructionTasksManagment> savedTasks = service.createAdmin(regSource, requestDTO);
        GenericResponse<List<SPBuildingConstructionTasksManagment>> response = new GenericResponse<>("SP Building Construction Task Saved Successfully", true, savedTasks);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update-building-construction-tasks")
    public ResponseEntity<GenericResponse<List<SPBuildingConstructionTasksManagment>>> updateTasks(
            @RequestParam RegSource regSource, @RequestBody List<SPBuildingConstructionTasksManagment> requestList) throws AccessDeniedException {
        List<SPBuildingConstructionTasksManagment> savedTasks = service.updateAdmin(regSource, requestList);
        GenericResponse<List<SPBuildingConstructionTasksManagment>> response = new GenericResponse<>("SP Building Construction Task Updated successfully", true, savedTasks);
        return ResponseEntity.ok(response);
    }
    
	@GetMapping("/get-building-construction-tasks-measureNames")
   	public ResponseEntity<List<TaskResponseDto>> getTaskDetails(
   	        @RequestParam(required = false) String serviceCategory,
   	        @RequestParam(required = false) String taskId,
   	        @RequestParam(required = false) String taskName,
   	     @RequestParam RegSource regSource) throws AccessDeniedException   {

   	    List<TaskResponseDto> result = service.getTaskDetails(serviceCategory, taskId, taskName,regSource);
   	    return ResponseEntity.ok(result);
   	}
}
