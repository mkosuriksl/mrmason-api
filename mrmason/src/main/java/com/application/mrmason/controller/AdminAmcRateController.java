package com.application.mrmason.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.application.mrmason.dto.ResponseAdminAmcDto;
import com.application.mrmason.dto.ResponseGetAdminPaintTasksManagemntDto;
import com.application.mrmason.dto.ResponseListAdminAmcRate;
import com.application.mrmason.entity.AdminAmcRate;
import com.application.mrmason.entity.AdminPaintTasksManagemnt;
import com.application.mrmason.service.AdminAmcRateService;

@RestController
@PreAuthorize("hasAuthority('Adm')")
public class AdminAmcRateController {
	@Autowired
	AdminAmcRateService adminService;
	ResponseListAdminAmcRate response = new ResponseListAdminAmcRate();

	@PostMapping("/addAdminAmc")
	public ResponseEntity<ResponseAdminAmcDto> addRentRequest(@RequestBody AdminAmcRate amc) {
		ResponseAdminAmcDto response = new ResponseAdminAmcDto();
		try {
			AdminAmcRate amcRates = adminService.addAdminamc(amc);
			if (amcRates != null) {
				response.setAdminAmcRates(amcRates);
				response.setMessage("AMC added successfully.");
				response.setStatus(true);
				return ResponseEntity.ok(response);
			} else {
				response.setMessage("Record already exists.");
				response.setStatus(false);
				return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.setMessage("An error occurred while adding the record.");
			response.setStatus(false);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}

	}

//	@GetMapping("/getAdminAmcRates")
//	public ResponseEntity<ResponseListAdminAmcRate> getAssetDetails(@RequestParam(required = false) String amcId,
//			@RequestParam(required = false) String planId, @RequestParam(required = false) String assetSubCat,
//			@RequestParam(required = false) String assetModel, @RequestParam(required = false) String assetBrand) {
//		ResponseListAdminAmcRate response = new ResponseListAdminAmcRate();
//		try {
//			List<AdminAmcRate> entity = adminService.getAmcRates(amcId, planId, assetSubCat, assetModel, assetBrand);
//			if (entity == null || entity.isEmpty()) {
//				response.setMessage("No data found for the given details.!");
//				response.setStatus(true);
//				return new ResponseEntity<>(response, HttpStatus.OK);
//			}
//			response.setMessage("Amc details fetched successfully.");
//			response.setStatus(true);
//			response.setData(entity);
//			return new ResponseEntity<>(response, HttpStatus.OK);
//		} catch (Exception e) {
//			response.setMessage(e.getMessage());
//			response.setStatus(false);
//			return new ResponseEntity<>(response, HttpStatus.OK);
//
//		}
//
//	}
	
	@GetMapping("/getAdminAmcRates")
	public ResponseEntity<ResponseListAdminAmcRate> getAssetDetails(@RequestParam(required = false) String amcId,
			@RequestParam(required = false) String planId, @RequestParam(required = false) String assetSubCat,
			@RequestParam(required = false) String assetModel, @RequestParam(required = false) String assetBrand,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
		
		Pageable pageable = PageRequest.of(page, size);
		Page<AdminAmcRate> srpqPage = adminService.getAmcRates(amcId, planId, assetSubCat, assetModel, assetBrand, pageable);

		ResponseListAdminAmcRate response = new ResponseListAdminAmcRate();

		response.setMessage("Amc details fetched successfully.");
		response.setStatus(true);
		response.setData(srpqPage.getContent());

		// Set pagination fields
		response.setCurrentPage(srpqPage.getNumber());
		response.setPageSize(srpqPage.getSize());
		response.setTotalElements(srpqPage.getTotalElements());
		response.setTotalPages(srpqPage.getTotalPages());

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PutMapping("/updateAdminAmcRates")
	public ResponseEntity<ResponseAdminAmcDto> updateAssetDetails(@RequestBody AdminAmcRate updateAmc) {
		ResponseAdminAmcDto response = new ResponseAdminAmcDto();
		AdminAmcRate amc = adminService.updateAmcRates(updateAmc);
		try {

			if (amc != null) {
				response.setAdminAmcRates(amc);
				response.setMessage("Admin Amc Rates updated successfully..");
				response.setStatus(true);
				return new ResponseEntity<>(response, HttpStatus.OK);
			} else {
				response.setMessage("failed to update/amcId not present");
				response.setStatus(false);
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
		} catch (Exception e) {
			response.setMessage(e.getMessage());
			response.setStatus(false);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}
}
