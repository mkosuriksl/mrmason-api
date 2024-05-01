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

import com.application.mrmason.dto.ResponceServiceDto;
import com.application.mrmason.dto.ServiceCategoryDto;
import com.application.mrmason.entity.ServiceCategory;
import com.application.mrmason.entity.ServiceCategoryMech;
import com.application.mrmason.service.ServiceCategoryService;

@RestController
@PreAuthorize("hasAuthority('EC')")
public class ServiceCategoryController {

	@Autowired
	public ServiceCategoryService categoryService;
	@PostMapping("/addServiceCategory")
	public ResponseEntity<?> addRentRequest(@RequestBody ServiceCategory service) {
		ResponceServiceDto response=new ResponceServiceDto();
		try {
			ServiceCategoryDto data= categoryService.addServiceCategory(service);
			if (data!= null) {
				response.setData(data);
				response.setMessage("Service category added successfully..");
				
				return ResponseEntity.ok(response);
			}
			return new ResponseEntity<>("A service is already present wih this sub category.!", HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
		}
	}
	
	@GetMapping("/getServiceCategory")
	public ResponseEntity<?> getServiceCategory(@RequestBody ServiceCategory service) {
		try {
			List<ServiceCategory> entity1 = categoryService.getServiceCategory(service);
			List<ServiceCategoryMech> entity2 = categoryService.getMechServiceCategory(service);
			if (!entity1.isEmpty()&& entity2.isEmpty()) {
				return new ResponseEntity<List<ServiceCategory>>(entity1, HttpStatus.OK);	
			}else if(entity1.isEmpty()&& !entity2.isEmpty()) {
				return new ResponseEntity<List<ServiceCategoryMech>>(entity2, HttpStatus.OK);
			}
			return new ResponseEntity<>("Invalid User.!", HttpStatus.BAD_REQUEST);

		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
		}

	}

	@PutMapping("/updateServiceCategory")
	public ResponseEntity<?> updateServiceCategory(@RequestBody ServiceCategory service) {
		ResponceServiceDto response=new ResponceServiceDto();
		try {
			ServiceCategoryDto data= categoryService.updateServiceCategory(service);
			if (data != null) {
				
				response.setData(data);
				response.setMessage("Service category updated successfully..");
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
			return new ResponseEntity<>("Invalid User.!", HttpStatus.BAD_REQUEST);
		} catch (Exception e) {

			return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
		}
	}
}
