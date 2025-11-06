package com.application.mrmason.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.application.mrmason.dto.GenericResponse;
import com.application.mrmason.entity.FrServiceRole;
import com.application.mrmason.service.FrServiceRolesService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/fr")
@RequiredArgsConstructor
public class FrServiceAvailablePostingLocationController {

	private final FrServiceRolesService service;

	@PostMapping("/service-role")
	public ResponseEntity<GenericResponse<FrServiceRole>> addServiceRole(@RequestBody FrServiceRole profile) {
		GenericResponse<FrServiceRole> response = service.addServiceRole(profile);
		return ResponseEntity.ok(response);
	}

}
