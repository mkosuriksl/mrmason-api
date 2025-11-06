package com.application.mrmason.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.application.mrmason.dto.GenericResponse;
import com.application.mrmason.entity.FrAvaiableLocation;
import com.application.mrmason.service.FrAvailableLocationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/fr")
@RequiredArgsConstructor
public class FrAvailableLocationController {

	private final FrAvailableLocationService service;

	@PostMapping("/available-location")
	public ResponseEntity<GenericResponse<FrAvaiableLocation>> addAvaiable(@RequestBody FrAvaiableLocation availablelocation) {
		GenericResponse<FrAvaiableLocation> response = service.addAvailableLocation(availablelocation);
		return ResponseEntity.ok(response);
	}

}
