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

import com.application.mrmason.dto.AdminServiceNameDto;
import com.application.mrmason.dto.ResponseAdminServiceDto;
import com.application.mrmason.entity.AdminServiceName;
import com.application.mrmason.service.impl.AdminServiceNameServiceImpl;

@RestController
@PreAuthorize("hasAuthority('Adm')")
public class AdminServiceNameController{

	@Autowired
	AdminServiceNameServiceImpl adminService;

	@PostMapping("/addAdminService")
	public ResponseEntity<?> addAdminServiceNameRequest(@RequestBody AdminServiceName service) {
		ResponseAdminServiceDto response = new ResponseAdminServiceDto();
		try {
			AdminServiceNameDto admin=adminService.addAdminServiceNameRequest(service);
			if ( admin != null) {
				response.setData(admin);
				response.setMessage("Admin Service added successfully..");
				response.setStatus(true);

				return ResponseEntity.ok(response);
			}
			response.setMessage("ServiceId is already present.!");
			response.setStatus(false);
			return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
		}
	}

	@GetMapping("/getAdminService")
	public ResponseEntity<?> getAdminServiceDetails(@RequestBody AdminServiceName service) {
		try {
			List<AdminServiceName> entity = adminService.getAdminServiceDetails(service);
			if (entity.isEmpty()) {
				return new ResponseEntity<>("Invalid User.!", HttpStatus.UNAUTHORIZED);
			}
			return new ResponseEntity<>(entity, HttpStatus.OK);

		} catch (Exception e) {

			return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
		}

	}

	@PutMapping("/updateAdminService")
	public ResponseEntity<?> updateAdminServiceDetails(@RequestBody AdminServiceName service) {
		ResponseAdminServiceDto response = new ResponseAdminServiceDto();
		try {

			AdminServiceNameDto admin=adminService.updateAdminServiceDetails(service);
			if (admin != null) {

				response.setData(admin);
				response.setMessage("Admin Service updated successfully..");
				response.setStatus(true);
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
			response.setMessage("Invalid User.!");
			response.setStatus(false);
			return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
		} catch (Exception e) {

			return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
		}
	}
}