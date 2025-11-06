package com.application.mrmason.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.application.mrmason.dto.GenericResponse;
import com.application.mrmason.entity.FrPositionType;
import com.application.mrmason.service.FrPositionTypeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/fr")
@RequiredArgsConstructor
public class FrPositionTypeController {

	private final FrPositionTypeService service;

	@PostMapping("/position-type")
	public ResponseEntity<GenericResponse<FrPositionType>> addPosition(@RequestBody FrPositionType position) {
		GenericResponse<FrPositionType> response = service.addPositionType(position);
		return ResponseEntity.ok(response);
	}

}
