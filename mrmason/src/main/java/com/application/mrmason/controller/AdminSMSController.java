package com.application.mrmason.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.application.mrmason.dto.ResponseAdminMailDto;
import com.application.mrmason.dto.ResponseAdminSmsDto;
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

//	@GetMapping("/getAllAdminSms")
//	public ResponseEntity<List<AdminSms>> getAllEmailDetails() {
//		return new ResponseEntity<>(apiService.getAllSMSDetails(), HttpStatus.OK);
//	}

	@GetMapping("/getAllAdminSms")
	public ResponseEntity<ResponseAdminSmsDto> getAllEmailDetails(
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size) {

	    Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending()); // Replace with actual column if needed
	    Page<AdminSms> smsPage = apiService.getAllSMSDetails(pageable);

	    ResponseAdminSmsDto response = new ResponseAdminSmsDto();
	    response.setMessage("SMS data fetched successfully");
	    response.setStatus(true);
	    response.setData(smsPage.getContent());
	    response.setCurrentPage(smsPage.getNumber());
	    response.setPageSize(smsPage.getSize());
	    response.setTotalElements(smsPage.getTotalElements());
	    response.setTotalPages(smsPage.getTotalPages());

	    return new ResponseEntity<>(response, HttpStatus.OK);
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
