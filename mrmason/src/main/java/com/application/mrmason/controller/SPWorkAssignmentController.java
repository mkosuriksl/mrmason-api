package com.application.mrmason.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.application.mrmason.dto.ResponseGetWorkerDto;
import com.application.mrmason.dto.Userdto;
import com.application.mrmason.entity.SPWorkAssignment;
import com.application.mrmason.entity.SpWorkers;
import com.application.mrmason.repository.UserDAO;
import com.application.mrmason.service.SPWorkAssignmentService;
import com.application.mrmason.service.SpWorkersService;
import com.application.mrmason.service.impl.UserService;


@RestController
@RequestMapping("/api/sp-work-assignment")
public class SPWorkAssignmentController {

    @Autowired
    private SPWorkAssignmentService service;
    
	ResponseGetWorkerDto response2=new ResponseGetWorkerDto();

	@Autowired
	UserDAO userRepo;
	
	@Autowired
	UserService userService;
	
	@Autowired
	private SpWorkersService spWorkersService;
	
    @PostMapping
    public ResponseEntity<SPWorkAssignment> createWorkAssignment(@RequestBody SPWorkAssignment request) {
        SPWorkAssignment savedAssignment = service.createAssignment(request);
        return ResponseEntity.ok(savedAssignment);
    }
    
    @GetMapping
    public ResponseEntity<ResponseGetWorkerDto> getAssetDetails(
            @RequestParam(required = false) String recId,
            @RequestParam(required = false) String servicePersonId,
            @RequestParam(required = false) String workerId,
            @RequestParam(required = false) String updatedBy) {

        ResponseGetWorkerDto response = new ResponseGetWorkerDto();
        try {
            List<SPWorkAssignment> assignments = service.getWorkers(recId, servicePersonId, workerId, updatedBy);
            response.setSpworkerAssignment(assignments);
            response.setStatus(true);

            if (!assignments.isEmpty()) {
                response.setMessage("Worker details retrieved successfully.");

                // Set user details
                String servicePerson = assignments.get(0).getServicePersonId();
                Userdto userDto = userService.getServiceProfile(servicePerson);
                response.setUserData(userDto);

                // Set worker details
                String worker = assignments.get(0).getWorkerId();
                SpWorkers workerData = spWorkersService.getWorkerById(worker);
                if (workerData != null) {
                    response.setWorkersData(Collections.singletonList(workerData));
                }

            } else {
                response.setMessage("No data found for details provided.");
            }

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            response.setStatus(false);
            response.setMessage("Error: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PutMapping
    public ResponseEntity<SPWorkAssignment> updateWorkAssignment(@RequestBody SPWorkAssignment updatedAssignment) {
        if (updatedAssignment.getRecId() == null) {
            return ResponseEntity.badRequest().body(null);
        }

        SPWorkAssignment updated = service.updateWorkAssignment(updatedAssignment);
        return ResponseEntity.ok(updated);
    }

}