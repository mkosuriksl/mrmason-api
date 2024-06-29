package com.application.mrmason.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.application.mrmason.dto.ResponseAdminSpQualiDto;
import com.application.mrmason.dto.ResponseAdminSpQualiDto1;
import com.application.mrmason.dto.ResponseAdminSpQualiDto2;
import com.application.mrmason.dto.ResponseAdminSpQualiDto3;
import com.application.mrmason.entity.AdminSpQualification;
import com.application.mrmason.service.AdminSpQualificationService;

@RestController
@PreAuthorize("hasAuthority('Adm')")
public class AdminSpQualificationController {

	@Autowired
	AdminSpQualificationService service;

	@PostMapping("/Adding-AdminSpQuilification")
	public ResponseEntity<?> addAdminSpQualification(@RequestBody AdminSpQualification education) {
		AdminSpQualification spQualification = service.addQualification(education);
		ResponseAdminSpQualiDto response = new ResponseAdminSpQualiDto();
		try {
			if (spQualification != null) {
				response.setMessage("Admin educatation quailification added successfully");
				response.setStatus(true);
				response.setAddQuilification(spQualification);
				return new ResponseEntity<>(response, HttpStatus.OK);
			} else {
				response.setMessage("Record already exists ");
				response.setStatus(false);
				response.setAddQuilification(spQualification);
				return new ResponseEntity<>(response, HttpStatus.OK);
			}

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.OK).body(e.getMessage());
		}

	}

	@PutMapping("/updateAdminQuilification")
	public ResponseEntity<?> updateAdminQuilification(@RequestBody AdminSpQualification updateQuili) {
		AdminSpQualification update = service.update(updateQuili);
		ResponseAdminSpQualiDto1 response = new ResponseAdminSpQualiDto1();
		try {
			if (update != null) {
				response.setMessage("Quilification updated successfully");
				response.setStatus(true);
				response.setUpdatedData(update);
				return new ResponseEntity<>(response, HttpStatus.OK);
			} else {
				response.setMessage("failed to updated/courseId not present");
				response.setStatus(false);
				response.setUpdatedData(update);
				return new ResponseEntity<>(response, HttpStatus.OK);
			}

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.OK).body(e.getMessage());
		}

	}

	@GetMapping("/getAdminSpQualification")
	public ResponseEntity<?> getAdminQualification(@RequestParam(required = false) String courseId,
			@RequestParam(required = false) String educationId, @RequestParam(required = false) String name,
			@RequestParam(required = false) String branchId, @RequestParam(required = false) String branchName) {

		ResponseAdminSpQualiDto2 response = new ResponseAdminSpQualiDto2();

		try {

			List<AdminSpQualification> get = service.getQualification(courseId, educationId, name, branchId,
					branchName);
			if (get != null && !get.isEmpty()) {
				response.setMessage("Admin education & qualification details");
				response.setStatus(true);
				response.setGetData(get);
				return new ResponseEntity<>(response, HttpStatus.OK);
			} else {
				response.setMessage("No details found for given parameters/check your parameters");
				response.setStatus(false);
				response.setGetData(get);
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.OK).body(e.getMessage());
		}

	}
	
	 @GetMapping("/getAllAdminQualificationDetails")
	    public ResponseEntity<?> getAllQualifications() {
		List<AdminSpQualification> all= service.getAllQualifications();
		ResponseAdminSpQualiDto3 response = new ResponseAdminSpQualiDto3();
		 try {
			 response.setMessage("Recieved all Admin Qualification Details");
			 response.setStatus(true);
			 response.setGetAll(all);
			 return new ResponseEntity<>(response,HttpStatus.OK);
		 }catch(Exception e) {
			 return ResponseEntity.status(HttpStatus.OK).body(e.getMessage());
		 }
	        
	    }

}
