package com.application.mrmason.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.application.mrmason.dto.GenericResponse;
import com.application.mrmason.dto.ResponseGetWorkProgressDetailsDto;
import com.application.mrmason.entity.WorkProgressDetails;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.service.WorkProgressDetailsService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/work-progress-details")
@Validated
public class WorkProgressDetailsController {

    @Autowired
    private WorkProgressDetailsService service;

    @PostMapping("/add")
    public ResponseEntity<GenericResponse<WorkProgressDetails>> addWorkProgressDetails(
            @Valid @RequestBody WorkProgressDetails request,@RequestParam RegSource regSource ) {

        WorkProgressDetails saved = service.addWorkProgressDetails(request,regSource);

        GenericResponse<WorkProgressDetails> response = new GenericResponse<>(
                "Work progress details added successfully", true, saved);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update")
    public ResponseEntity<GenericResponse<WorkProgressDetails>> updateWorkProgressDetails(
            @Valid @RequestBody WorkProgressDetails request,@RequestParam RegSource regSource ) {

        WorkProgressDetails updated = service.updateWorkProgressDetails(request,regSource);
        return ResponseEntity.ok(new GenericResponse<>("Work progress details updated successfully", true,
                updated));
    }

    @GetMapping("/get")
    public ResponseEntity<ResponseGetWorkProgressDetailsDto> getHeaderWorkOrder(
            @RequestParam(required = false) String orderNoDate,
            @RequestParam(required = false) String orderNo, @RequestParam(required = false) String workDescription,
            @RequestParam(required = false) String taskId, @RequestParam(required = false) String subTaskId,
            Pageable pageable) {

        Page<WorkProgressDetails> pageResult = service.get(orderNoDate, orderNo, workDescription, taskId,
        		subTaskId,pageable);

        ResponseGetWorkProgressDetailsDto response = new ResponseGetWorkProgressDetailsDto();
        response.setMessage("Work progress details Retrieved Successfully");
        response.setStatus(true);
        response.setWorkProgressDetails(pageResult.getContent());
        response.setCurrentPage(pageResult.getNumber());
        response.setPageSize(pageResult.getSize());
        response.setTotalElements(pageResult.getTotalElements());
        response.setTotalPages(pageResult.getTotalPages());

        return ResponseEntity.ok(response);
    }

}
