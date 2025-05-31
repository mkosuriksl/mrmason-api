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
import com.application.mrmason.dto.SPElectricalTaskRequestDTO;
import com.application.mrmason.dto.TaskResponseDto;
import com.application.mrmason.entity.SPElectricalTasksManagemnt;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.service.SPElectricalTasksManagemntService;

@RestController
@RequestMapping("/sptasks")
public class SPElectricalTasksManagemntController {

    @Autowired
    private SPElectricalTasksManagemntService service;

    @PostMapping("/add-electrical-tasks")
    public ResponseEntity<GenericResponse<List<SPElectricalTasksManagemnt>>> addTasks(
            @RequestParam RegSource regSource, @RequestBody SPElectricalTaskRequestDTO requestDTO) throws AccessDeniedException {

        List<SPElectricalTasksManagemnt> savedTasks = service.createAdmin(regSource, requestDTO);

        GenericResponse<List<SPElectricalTasksManagemnt>> response = new GenericResponse<>("SP Electrical Task Saved Successfully",
                true, savedTasks);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/update-electrical-tasks")
    public ResponseEntity<GenericResponse<List<SPElectricalTasksManagemnt>>> updateTasks(
            @RequestParam RegSource regSource, @RequestBody List<SPElectricalTasksManagemnt> requestList) throws AccessDeniedException {

        List<SPElectricalTasksManagemnt> savedTasks = service.updateAdmin(regSource, requestList);

        GenericResponse<List<SPElectricalTasksManagemnt>> response = new GenericResponse<>("SP Electrical Task Updated successfully",
                true, savedTasks);

        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/get-electrical-tasks-measureNames")
	public ResponseEntity<List<TaskResponseDto>> getTaskDetails(
	        @RequestParam(required = false) String serviceCategory,
	        @RequestParam(required = false) String taskId,
	        @RequestParam(required = false) String taskName)   {

	    List<TaskResponseDto> result = service.getTaskDetails(serviceCategory, taskId, taskName);
	    return ResponseEntity.ok(result);
	}

}
