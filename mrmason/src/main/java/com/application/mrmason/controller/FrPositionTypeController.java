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
import com.application.mrmason.dto.ResponsePositionTypeDto;
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

	@GetMapping("/get-positionType")
	public ResponseEntity<ResponsePositionTypeDto> getPositionType(@RequestParam(required = false) String frUserId,
			@RequestParam(required = false) List<String> positionType, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {

		ResponsePositionTypeDto response = service.getPositionType(frUserId, positionType, page, size);

		return ResponseEntity.ok(response);
	}

	@PutMapping("/update-positionType")
	public ResponseEntity<GenericResponse<FrPositionType>> updatePositionType(@RequestBody FrPositionType dto) {

		GenericResponse<FrPositionType> response = service.updatePositionType(dto);

		return ResponseEntity.ok(response);
	}

}
