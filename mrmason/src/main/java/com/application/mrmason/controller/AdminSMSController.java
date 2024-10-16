package com.application.mrmason.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.application.mrmason.dto.ResponseAdminMailDto;
import com.application.mrmason.entity.AdminSms;
import com.application.mrmason.service.AdminSMSService;

@RestController
@PreAuthorize("hasAuthority('Adm')")
public class AdminSMSController {

	@Autowired
	private AdminSMSService apiService;

	ResponseAdminMailDto response = new ResponseAdminMailDto();

	@PostMapping("/addAdminSms")
	public ResponseEntity<ResponseAdminMailDto> newAdminAsset(@RequestBody AdminSms api) {
		try {
			ResponseAdminMailDto response = apiService.addApiRequest(api);
			return ResponseEntity.ok(response);

		} catch (Exception e) {
			response.setMessage(e.getMessage());
			response.setStatus(false);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}

	@GetMapping("/getAllAdminSms")
	public ResponseEntity<List<AdminSms>> getAllEmailDetails() {
		return new ResponseEntity<>(apiService.getAllSMSDetails(), HttpStatus.OK);
	}

	@PutMapping("/updateAdminSms")
	public ResponseEntity<ResponseAdminMailDto> updateAssetDetails(@RequestBody AdminSms api) {
		try {
			ResponseAdminMailDto response = apiService.updateApiRequest(api);
			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (Exception e) {
			response.setMessage(e.getMessage());
			response.setStatus(false);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}
}
