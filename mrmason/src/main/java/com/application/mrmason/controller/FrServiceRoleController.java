package com.application.mrmason.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.application.mrmason.dto.GenericResponse;
import com.application.mrmason.dto.ResponseServiceRoleDto;
import com.application.mrmason.entity.FrPositionType;
import com.application.mrmason.entity.FrServiceRole;
import com.application.mrmason.service.FrServiceRolesService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/fr")
@RequiredArgsConstructor
public class FrServiceRoleController {

	private final FrServiceRolesService service;

	@PostMapping("/service-role")
	public ResponseEntity<GenericResponse<FrServiceRole>> addServiceRole(@RequestBody FrServiceRole profile) {
		GenericResponse<FrServiceRole> response = service.addServiceRole(profile);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/get-serviceRole")
	public ResponseEntity<ResponseServiceRoleDto> getServiceRole(@RequestParam(required = false) String frUserId,
			@RequestParam(required = false) List<String> training,
			@RequestParam(required = false) List<String> developer, @RequestParam(required = false) String interviewer,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

		ResponseServiceRoleDto response = service.getServiceRole(frUserId, training, developer, interviewer, page,
				size);

		return ResponseEntity.ok(response);
	}
	
	@PutMapping("/update-serviceRole")
	public ResponseEntity<GenericResponse<FrServiceRole>> updateServiceRole(@RequestBody FrServiceRole dto) {

		GenericResponse<FrServiceRole> response = service.updateServiceRole(dto);

		return ResponseEntity.ok(response);
	}


}
