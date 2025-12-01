package com.application.mrmason.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.application.mrmason.dto.AdminBhatServiceNameDto;
import com.application.mrmason.dto.ResponseAdminBhatServiceDto;
import com.application.mrmason.dto.ResponseListAdminBhatServiceDto;
import com.application.mrmason.entity.AdminBhatServiceName;
import com.application.mrmason.service.impl.AdminBhatServiceNameServiceImpl;

@RestController
public class AdminBhatServiceNameController {

	@Autowired
	AdminBhatServiceNameServiceImpl adminService;

	@PostMapping("/addAdminBhatService")
	public ResponseEntity<ResponseAdminBhatServiceDto> addAdminServiceNameRequest(
			@RequestBody AdminBhatServiceName service) {
		ResponseAdminBhatServiceDto response = new ResponseAdminBhatServiceDto();
		try {
			AdminBhatServiceNameDto admin = adminService.addAdminBhatServiceNameRequest(service);
			if (admin != null) {
				response.setData(admin);
				response.setMessage("Admin Bhat Service added successfully..");
				response.setStatus(true);

				return ResponseEntity.ok(response);
			}
			response.setMessage("ServiceId is already present.!");
			response.setStatus(false);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setMessage(e.getMessage());
			response.setStatus(false);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}

	@GetMapping("/getAdminBhatService")
	public ResponseEntity<ResponseListAdminBhatServiceDto> getAdminServiceDetails(
			@RequestParam(required = false) String serviceId, @RequestParam(required = false) String serviceName,
			@RequestParam(required = false) String serviceSubCat, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {

		ResponseListAdminBhatServiceDto response = new ResponseListAdminBhatServiceDto();
		try {
			Pageable pageable = PageRequest.of(page, size, Sort.by("addedDate").descending());

			Page<AdminBhatServiceName> resultPage = adminService.getAdminBhatServiceDetails(serviceId, serviceName,
					serviceSubCat, pageable);

			if (resultPage.isEmpty()) {
				response.setMessage("No service data found.");
				response.setStatus(false);
			} else {
				response.setMessage("Service data fetched successfully.");
				response.setStatus(true);
				response.setData(resultPage.getContent());
				response.setCurrentPage(resultPage.getNumber());
				response.setPageSize(resultPage.getSize());
				response.setTotalElements(resultPage.getTotalElements());
				response.setTotalPages(resultPage.getTotalPages());
			}

			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setMessage("Error: " + e.getMessage());
			response.setStatus(false);
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/updateAdminBhatService")
	public ResponseEntity<ResponseAdminBhatServiceDto> updateAdminServiceDetails(
			@RequestBody AdminBhatServiceName service) {
		ResponseAdminBhatServiceDto response = new ResponseAdminBhatServiceDto();
		try {

			AdminBhatServiceNameDto admin = adminService.updateAdminBhatServiceDetails(service);
			if (admin != null) {

				response.setData(admin);
				response.setMessage("Admin Bhat Service updated successfully..");
				response.setStatus(true);
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
			response.setMessage("Invalid User.!");
			response.setStatus(false);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {

			response.setMessage(e.getMessage());
			response.setStatus(false);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}
}
