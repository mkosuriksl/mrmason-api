package com.application.mrmason.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.application.mrmason.dto.GenericResponse;
import com.application.mrmason.entity.FrAvailable;
import com.application.mrmason.service.FrAvaiableService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/fr")
@RequiredArgsConstructor
public class FrAvaibaleController {

	private final FrAvaiableService service;

	@PostMapping("/avaiable")
	public ResponseEntity<GenericResponse<FrAvailable>> addAvaiable(@RequestBody FrAvailable profile) {
		GenericResponse<FrAvailable> response = service.addAvaiable(profile);
		return ResponseEntity.ok(response);
	}

}
