package com.application.mrmason.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.application.mrmason.dto.GenericResponse;
import com.application.mrmason.dto.ResponseGetWorkerDto;
import com.application.mrmason.entity.SPWorkAssignment;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.repository.UserDAO;
import com.application.mrmason.service.SPWorkAssignmentService;
import com.application.mrmason.service.SpWorkersService;
import com.application.mrmason.service.impl.UserService;

@RestController
@RequestMapping("/api/sp-work-assignment")
public class SPWorkAssignmentController {

	@Autowired
	private SPWorkAssignmentService service;

	ResponseGetWorkerDto response2 = new ResponseGetWorkerDto();

	@Autowired
	UserDAO userRepo;

	@Autowired
	UserService userService;

	@Autowired
	private SpWorkersService spWorkersService;

	@PostMapping("/add")
	public ResponseEntity<GenericResponse<List<SPWorkAssignment>>> createWorkAssignment(
			@RequestBody SPWorkAssignment request, @RequestParam RegSource regSource) {

		List<SPWorkAssignment> savedAssignments = service.createAssignment(request, regSource);

		GenericResponse<List<SPWorkAssignment>> response = new GenericResponse<>(
				"Work assignments created successfully", true, savedAssignments);

		return ResponseEntity.ok(response);
	}

	@GetMapping
	public ResponseEntity<ResponseGetWorkerDto> getAssetDetails(@RequestParam(required = false) String recId,
			@RequestParam(required = false) String workerIdWorkOrdIdLine, @RequestParam(required = false) String workerId,
			@RequestParam(required = false) String updatedBy,@RequestParam(required = false) String location,@RequestParam(required = false)String available,
			@RequestParam(required = false)@DateTimeFormat String fromDateOfWork,
	        @RequestParam(required = false)@DateTimeFormat String toDateOfWork,@RequestParam(required = false) String spId, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {

		Pageable pageable = PageRequest.of(page, size);
		Page<SPWorkAssignment> workersPage = service.getWorkers(recId, workerIdWorkOrdIdLine, workerId, updatedBy,location,available,fromDateOfWork,toDateOfWork,spId, pageable);
		ResponseGetWorkerDto response = new ResponseGetWorkerDto();

		response.setMessage("Worker details retrieved successfully.");
		response.setStatus(true);
		response.setSpworkerAssignment(workersPage.getContent());
		response.setUserData(null); // or your actual userData

		// Set pagination fields
		response.setCurrentPage(workersPage.getNumber());
		response.setPageSize(workersPage.getSize());
		response.setTotalElements(workersPage.getTotalElements());
		response.setTotalPages(workersPage.getTotalPages());

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

//    @GetMapping
//    public ResponseEntity<ResponseGetWorkerDto> getAssetDetails(
//            @RequestParam(required = false) String recId,
//            @RequestParam(required = false) String servicePersonId,
//            @RequestParam(required = false) String workerId,
//            @RequestParam(required = false) String updatedBy) {
//
//        ResponseGetWorkerDto response = new ResponseGetWorkerDto();
//        try {
//            List<SPWorkAssignment> assignments = service.getWorkers(recId, servicePersonId, workerId, updatedBy);
//            response.setSpworkerAssignment(assignments);
//            response.setStatus(true);
//
//            if (!assignments.isEmpty()) {
//                response.setMessage("Worker details retrieved successfully.");
//
//                // Set user details
//                String servicePerson = assignments.get(0).getServicePersonId();
//                Userdto userDto = userService.getServiceProfile(servicePerson);
//                response.setUserData(userDto);
//
//                // Set worker details
//                String worker = assignments.get(0).getWorkerId();
//                SpWorkers workerData = spWorkersService.getWorkerById(worker);
//                if (workerData != null) {
//                    response.setWorkersData(Collections.singletonList(workerData));
//                }
//
//            } else {
//                response.setMessage("No data found for details provided.");
//            }
//
//            return new ResponseEntity<>(response, HttpStatus.OK);
//
//        } catch (Exception e) {
//            response.setStatus(false);
//            response.setMessage("Error: " + e.getMessage());
//            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

	@PutMapping
	public ResponseEntity<SPWorkAssignment> updateWorkAssignment(@RequestBody SPWorkAssignment updatedAssignment,
			@RequestParam RegSource regSource) {
		if (updatedAssignment.getRecId() == null) {
			return ResponseEntity.badRequest().body(null);
		}

		SPWorkAssignment updated = service.updateWorkAssignment(updatedAssignment, regSource);
		return ResponseEntity.ok(updated);
	}

}