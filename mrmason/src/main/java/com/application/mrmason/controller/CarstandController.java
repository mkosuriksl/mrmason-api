package com.application.mrmason.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.application.mrmason.dto.UserChargeResponseDTO;
import com.application.mrmason.service.UserServiceChargesService;

@RestController
@RequestMapping("/carstand-api")
public class CarstandController {

	@Autowired
	UserServiceChargesService service;

	final String code = "eyJhbGciOiJIUzI1NiJ9eyJwaG9uZSI6Ijk1NTQxMTg1OTIiLCJzdWIiOiJ0ZXN0eUR";

	@GetMapping("/getUserServiceCharegs-withoutSecurity")
	public ResponseEntity<?> getUserCharge(@RequestParam(required = false) String serviceChargeKey,
			@RequestParam(required = false) String serviceId, @RequestParam(required = false) String location,
			@RequestParam(required = false) String brand, @RequestParam(required = false) String model,
			@RequestParam(required = false) String userId, @RequestParam(required = false) String subcategory,
			@RequestHeader String token) {

		if (token.equals(code)) {
			List<UserChargeResponseDTO> response = service.getUserCharges(serviceChargeKey, serviceId, location, brand,
					model, userId, subcategory);

			if (response == null) {
				return ResponseEntity.ok("No Records Found");
			}

			return ResponseEntity.ok(response);
		}
		return null;
	}

}
