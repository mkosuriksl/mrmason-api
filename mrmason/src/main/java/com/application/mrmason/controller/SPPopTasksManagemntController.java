package com.application.mrmason.controller;

import java.nio.file.AccessDeniedException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.application.mrmason.dto.GenericResponse;
import com.application.mrmason.dto.SPPopTaskRequestDTO;
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
}
