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

import com.application.mrmason.dto.AdminServiceNameDto;
import com.application.mrmason.dto.ResponseAdminServiceDto;
import com.application.mrmason.dto.ResponseListAdminServiceDto;
import com.application.mrmason.entity.AdminServiceName;
import com.application.mrmason.service.impl.AdminServiceNameServiceImpl;

@RestController
@PreAuthorize("hasAuthority('Adm')")
public class AdminServiceNameController{

	@Autowired
	AdminServiceNameServiceImpl adminService;

	@PostMapping("/addAdminService")
	public ResponseEntity<ResponseAdminServiceDto> addAdminServiceNameRequest(@RequestBody AdminServiceName service) {
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
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setMessage(e.getMessage());
			response.setStatus(false);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}

//	@GetMapping("/getAdminService")
//	public ResponseEntity<ResponseListAdminServiceDto> getAdminServiceDetails(@RequestParam(required = false) String serviceId ,@RequestParam(required = false) String serviceName,@RequestParam(required = false) String serviceSubCat) {
//		ResponseListAdminServiceDto response=new ResponseListAdminServiceDto();
//		try {
//			
//			List<AdminServiceName> entity = adminService.getAdminServiceDetails(serviceId, serviceName, serviceSubCat);
//			if (entity.isEmpty()) {
//				response.setMessage("Invalid User.!");
//				response.setStatus(false);
//				return new ResponseEntity<>(response, HttpStatus.OK);
//			}
//			response.setMessage("Service data fetched successfully.!");
//			response.setStatus(true);
//			response.setData(entity);
//			return new ResponseEntity<>(response, HttpStatus.OK);
//
//		} catch (Exception e) {
//			response.setMessage(e.getMessage());
//			response.setStatus(false);
//			return new ResponseEntity<>(response, HttpStatus.OK);
//		}
//
//	}

	@GetMapping("/getAdminService")
	public ResponseEntity<ResponseListAdminServiceDto> getAdminServiceDetails(
	        @RequestParam(required = false) String serviceId,
	        @RequestParam(required = false) String serviceName,
	        @RequestParam(required = false) String serviceSubCat,
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size) {

	    ResponseListAdminServiceDto response = new ResponseListAdminServiceDto();
	    try {
	        Pageable pageable = PageRequest.of(page, size, Sort.by("addedDate").descending());

	        Page<AdminServiceName> resultPage = adminService.getAdminServiceDetails(serviceId, serviceName, serviceSubCat, pageable);

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

	
	@PutMapping("/updateAdminService")
	public ResponseEntity<ResponseAdminServiceDto> updateAdminServiceDetails(@RequestBody AdminServiceName service) {
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
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {

			response.setMessage(e.getMessage());
			response.setStatus(false);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}
}