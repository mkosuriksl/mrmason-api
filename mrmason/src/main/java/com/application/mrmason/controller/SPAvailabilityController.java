package com.application.mrmason.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.application.mrmason.dto.UpdateAvailableDto;
import com.application.mrmason.entity.SPAvailability;
import com.application.mrmason.service.impl.SPAvailabilityServiceIml;

@RestController
@PreAuthorize("hasAuthority('Developer')")
public class SPAvailabilityController {

	@Autowired
	SPAvailabilityServiceIml spAvailableService;

	@PostMapping("/sp-update-avalability")
	public ResponseEntity<?> updateAvailabilityOfAddress(@RequestBody SPAvailability available) {
		try {

			SPAvailability availability = spAvailableService.availability(available);

			if (availability != null) {
				return new ResponseEntity<>("Address updated successfully", HttpStatus.OK);
			}
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid user");

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
		
	}

	@GetMapping("/sp-get-update-availability")
	public ResponseEntity<?> getAddress(@RequestBody UpdateAvailableDto upDto) {
		String mobile = upDto.getMobile();
		String email = upDto.getEmail();

		List<SPAvailability> availability = spAvailableService.getAvailability(email, mobile);
		try {
			if (!availability.isEmpty()) {
				return new ResponseEntity<>(availability, HttpStatus.OK);
			} else {
				return new ResponseEntity<>("No user found for the given parameters.", HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}

	}
}