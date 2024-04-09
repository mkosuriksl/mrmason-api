package com.application.mrmason.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.application.mrmason.dto.ResponceServiceDto;
import com.application.mrmason.entity.ServiceCategory;
import com.application.mrmason.service.ServiceCategoryService;

@RestController
public class ServiceCategoryController {

	@Autowired
	public ServiceCategoryService categoryService;
	@PostMapping("/addServiceCategory")
	public ResponseEntity<?> addRentRequest(@RequestBody ServiceCategory service) {
		ResponceServiceDto response=new ResponceServiceDto();
		try {
			if (categoryService.addServiceCategory(service) != null) {
				response.setData(categoryService.addServiceCategory(service));
				response.setMessage("Service category added successfully..");
				
				return ResponseEntity.ok(response);
			}
			return new ResponseEntity<>("Invalid User.!", HttpStatus.UNAUTHORIZED);
		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
		}
	}
	
	@GetMapping("/getServiceCategory")
	public ResponseEntity<?> getAssetDetails(@RequestBody ServiceCategory service) {
		try {
			List<ServiceCategory> entity = categoryService.getServiceCategory(service);
			if (entity.isEmpty()) {
				return new ResponseEntity<>("Invalid User.!", HttpStatus.UNAUTHORIZED);
			}
			return new ResponseEntity<List<ServiceCategory>>(entity, HttpStatus.OK);

		} catch (Exception e) {

			return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
		}

	}

	@PutMapping("/updateServiceCategory")
	public ResponseEntity<?> updateAssetDetails(@RequestBody ServiceCategory service) {
		ResponceServiceDto response=new ResponceServiceDto();
		try {

			if (categoryService.updateServiceCategory(service) != null) {
				
				response.setData(categoryService.updateServiceCategory(service));
				response.setMessage("Service category updated successfully..");
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
			return new ResponseEntity<>("Invalid User.!", HttpStatus.UNAUTHORIZED);
		} catch (Exception e) {

			return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
		}
	}
}
