package com.application.mrmason.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.application.mrmason.dto.ResponceServiceDto;
import com.application.mrmason.dto.ServiceCategoryDto;
import com.application.mrmason.entity.ServiceCategory;
import com.application.mrmason.service.ServiceCategoryService;

@RestController

public class ServiceCategoryController {

	@Autowired
	public ServiceCategoryService categoryService;
	@PreAuthorize("hasAuthority('Adm')")
	@PostMapping("/addServiceCategory")
	public ResponseEntity<?> addRentRequest(@RequestBody ServiceCategory service) {
		ResponceServiceDto response=new ResponceServiceDto();
		try {
			ServiceCategoryDto data= categoryService.addServiceCategory(service);
			if (data!= null) {
				response.setData(data);
				response.setMessage("Service category added successfully..");
				response.setStatus(true);
				return ResponseEntity.ok(response);
			}
			response.setMessage("A service is already present wih this sub category.!");
			response.setStatus(false);
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
		}
	}
	@PreAuthorize("hasAuthority('Adm')")
	@GetMapping("/getServiceCategory")
	public ResponseEntity<?> getServiceCategory(@RequestBody ServiceCategory service) {
		try {
			List<ServiceCategory> entity = categoryService.getServiceCategory(service);
			
			if (!entity.isEmpty()) {
				return new ResponseEntity<List<ServiceCategory>>(entity, HttpStatus.OK);	
			}
			return new ResponseEntity<>("Invalid User.!", HttpStatus.BAD_REQUEST);

		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
		}

	}
	
	@GetMapping("/getServiceCategory/civil/{serviceCategory}")
	public ResponseEntity<?> getServiceCategoryCivil(@PathVariable String serviceCategory) {
		try {
			List<ServiceCategory> entity = categoryService.getServiceCategoryCivil(serviceCategory);
			
			if (!entity.isEmpty()) {
				return new ResponseEntity<List<ServiceCategory>>(entity, HttpStatus.OK);	
			}
			return new ResponseEntity<>("Invalid User.!", HttpStatus.BAD_REQUEST);

		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
		}

	}
	
	@GetMapping("/getServiceCategory/nonCivil/{serviceCategory}")
	public ResponseEntity<?> getServiceCategoryNonCivil(@PathVariable String serviceCategory) {
		try {
			List<ServiceCategory> entity = categoryService.getServiceCategoryNonCivil(serviceCategory);
			
			if (!entity.isEmpty()) {
				return new ResponseEntity<List<ServiceCategory>>(entity, HttpStatus.OK);	
			}
			return new ResponseEntity<>("Invalid User.!", HttpStatus.BAD_REQUEST);

		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
		}

	}
	@PreAuthorize("hasAuthority('Adm')")
	@PutMapping("/updateServiceCategory")
	public ResponseEntity<?> updateServiceCategory(@RequestBody ServiceCategory service) {
		ResponceServiceDto response=new ResponceServiceDto();
		try {
			ServiceCategoryDto data= categoryService.updateServiceCategory(service);
			if (data != null) {
				
				response.setData(data);
				response.setMessage("Service category updated successfully..");
				response.setStatus(true);
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
			response.setMessage("Invalid User.!");
			response.setStatus(false);
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {

			return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
		}
	}
}
