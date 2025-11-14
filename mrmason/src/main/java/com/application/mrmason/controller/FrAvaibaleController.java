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
import com.application.mrmason.dto.ResponseAvailableDto;
import com.application.mrmason.entity.FrAvailable;
import com.application.mrmason.service.FrAvaiableService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/fr")
@RequiredArgsConstructor
public class FrAvaibaleController {

	private final FrAvaiableService service;

	@PostMapping("/available")
	public ResponseEntity<GenericResponse<FrAvailable>> addAvaiable(@RequestBody FrAvailable profile) {
		GenericResponse<FrAvailable> response = service.addAvaiable(profile);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/get-available")
	public ResponseEntity<ResponseAvailableDto> getAvaiable(@RequestParam(required = false) String frUserId,
			@RequestParam(required = false) String remote, @RequestParam(required = false) String onsite,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

		ResponseAvailableDto response = service.getAvailable(frUserId, remote, onsite, page, size);

		return ResponseEntity.ok(response);
	}

	@PutMapping("/update-available")
	public ResponseEntity<GenericResponse<FrAvailable>> updateLocation(@RequestBody FrAvailable dto) {

		GenericResponse<FrAvailable> response = service.updateAvaiable(dto);

		return ResponseEntity.ok(response);
	}
}
