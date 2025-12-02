package com.application.mrmason.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.application.mrmason.dto.GenericResponse;
import com.application.mrmason.dto.ResponseGetSparePartDto;
import com.application.mrmason.dto.SparePartDto;
import com.application.mrmason.dto.SparePartEntity;
import com.application.mrmason.entity.UserType;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.service.SparePartService;

@RestController
@RequestMapping("/spare-parts")
public class SparePartController {

	@Autowired
	SparePartService service;

	@PostMapping("/add")
	public GenericResponse<SparePartDto> addSparePart(@RequestBody SparePartDto dto, @RequestParam RegSource regSource,
			@RequestParam UserType userType) {

		SparePartDto savedDto = service.addSparePart(dto, regSource, userType);

		return new GenericResponse<>(

				"Spare part added successfully", true, savedDto);
	}

	@PutMapping("/update")
	public GenericResponse<SparePartDto> updateSparePart(@RequestBody SparePartDto dto,
			@RequestParam RegSource regSource, @RequestParam UserType userType) {

		SparePartDto updatedDto = service.updateSparePart(dto, regSource, userType);

		return new GenericResponse<>("Spare part updated successfully", true, updatedDto);
	}

	@GetMapping("/get")
	public ResponseEntity<ResponseGetSparePartDto> getServiceRequestPaintQuotationService(
			@RequestParam(required = false) String requestId, @RequestParam(required = false) String sparePart,
			@RequestParam(required = false) String brand, @RequestParam(required = false) String model,
			@RequestParam(required = false) String userId, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) throws AccessDeniedException {

		Pageable pageable = PageRequest.of(page, size);
		Page<SparePartEntity> srpqPage = service.getSparePart(requestId, sparePart, brand, model, userId, pageable);
		ResponseGetSparePartDto response = new ResponseGetSparePartDto();

		response.setMessage("Spare part details retrieved successfully.");
		response.setStatus(true);
		response.setSparePartdto(srpqPage.getContent());

		// Set pagination fields
		response.setCurrentPage(srpqPage.getNumber());
		response.setPageSize(srpqPage.getSize());
		response.setTotalElements(srpqPage.getTotalElements());
		response.setTotalPages(srpqPage.getTotalPages());

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
