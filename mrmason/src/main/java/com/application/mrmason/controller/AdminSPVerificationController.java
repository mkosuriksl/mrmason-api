package com.application.mrmason.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.application.mrmason.dto.GenericResponse;
import com.application.mrmason.entity.AdminPaintTasksManagemnt;
import com.application.mrmason.entity.AdminSpVerification;
import com.application.mrmason.service.AdminPaintTasksManagemntService;
import com.application.mrmason.service.impl.AdminSPVerificationService;

@RestController
@RequestMapping("/admin-sp-verification")
public class AdminSPVerificationController {

	@Autowired
	private AdminSPVerificationService service;

	@PutMapping("/update")
	public ResponseEntity<GenericResponse<List<AdminSpVerification>>> addTasks(
			@RequestBody List<AdminSpVerification> requestList) {

		List<AdminSpVerification> savedTasks = service.updateAdmin(requestList);

		GenericResponse<List<AdminSpVerification>> response = new GenericResponse<>("Admin SP Verification  Updated successfully",
				true, savedTasks);

		return ResponseEntity.ok(response);
	}

}
