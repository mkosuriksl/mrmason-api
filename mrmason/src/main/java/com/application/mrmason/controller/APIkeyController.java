package com.application.mrmason.controller;

import java.nio.file.AccessDeniedException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.application.mrmason.dto.GenericResponse;
import com.application.mrmason.dto.ResponseGetAPIkeyDto;
import com.application.mrmason.entity.APIKEY;
import com.application.mrmason.service.APIKeyService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class APIkeyController {

	@Autowired
	private APIKeyService apiKeyService;

	@PostMapping("/add-apikey")
	public ResponseEntity<GenericResponse<APIKEY>> addApiKey(@RequestBody APIKEY request) {
		APIKEY savedKey = apiKeyService.addApiKey(request);

		GenericResponse<APIKEY> response = new GenericResponse<>("API key saved successfully", true, savedKey);

		return ResponseEntity.ok(response);
	}
	
	@PutMapping("/update-apikey")
	public ResponseEntity<GenericResponse<APIKEY>> updateApiKey(@RequestBody APIKEY request) {
	    try {
	        APIKEY updatedKey = apiKeyService.updateApiKey(request);
	        GenericResponse<APIKEY> response = new GenericResponse<>(
	                "API key updated successfully",
	                true,
	                updatedKey
	        );
	        return ResponseEntity.ok(response);
	    } catch (Exception e) {
	        GenericResponse<APIKEY> response = new GenericResponse<>(
	                "Error: " + e.getMessage(),
	                false,
	                null
	        );
	        return ResponseEntity.badRequest().body(response);
	    }
	}
	
	@GetMapping("/get-apikey")
	public ResponseEntity<ResponseGetAPIkeyDto> getTask(
			@RequestParam(required = false) String apiKey,
			@RequestParam(required = false) String updatedBy, 
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size)
			throws AccessDeniedException {

		Pageable pageable = PageRequest.of(page, size);
		Page<APIKEY> srpqPage = apiKeyService.get(apiKey, updatedBy,pageable);
		ResponseGetAPIkeyDto response = new ResponseGetAPIkeyDto();

		response.setMessage("API Key is retrieved successfully.");
		response.setStatus(true);
		response.setApikeys(srpqPage.getContent());

		// Set pagination fields
		response.setCurrentPage(srpqPage.getNumber());
		response.setPageSize(srpqPage.getSize());
		response.setTotalElements(srpqPage.getTotalElements());
		response.setTotalPages(srpqPage.getTotalPages());

		return new ResponseEntity<>(response, HttpStatus.OK);
	}


}
