package com.application.mrmason.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.application.mrmason.dto.GenericResponse;
import com.application.mrmason.dto.ResponseAvailableLocationDto;
import com.application.mrmason.entity.FrAvaiableLocation;
import com.application.mrmason.service.FrAvailableLocationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/fr")
@RequiredArgsConstructor
public class FrAvailableLocationController {

	private final FrAvailableLocationService service;

	@PostMapping("/available-location")
	public ResponseEntity<GenericResponse<FrAvaiableLocation>> addAvaiable(
			@RequestBody FrAvaiableLocation availablelocation) {
		GenericResponse<FrAvaiableLocation> response = service.addAvailableLocation(availablelocation);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/get-available-location")
	public ResponseEntity<ResponseAvailableLocationDto> getLocation(@RequestParam(required = false) String frUserId,
			@RequestParam(required = false) String city, @RequestParam(required = false) String countrycode,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

		ResponseAvailableLocationDto response = service.getAvailableLocations(frUserId, city, countrycode, page, size);

		return ResponseEntity.ok(response);
	}

	@PutMapping("/update-location")
	public ResponseEntity<GenericResponse<FrAvaiableLocation>> updateLocation(@RequestBody FrAvaiableLocation dto) {

		GenericResponse<FrAvaiableLocation> response = service.updateLocation(dto);

		return ResponseEntity.ok(response);
	}

}
