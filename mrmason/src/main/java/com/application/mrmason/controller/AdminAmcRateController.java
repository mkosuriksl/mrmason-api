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

import com.application.mrmason.dto.ResponseAdminAmcDto;
import com.application.mrmason.dto.ResponseListAdminAmcRate;
import com.application.mrmason.entity.AdminAmcRate;
import com.application.mrmason.service.AdminAmcRateService;
@RestController
@PreAuthorize("hasAuthority('Adm')")
public class AdminAmcRateController {
	@Autowired
	public AdminAmcRateService adminService;
	ResponseListAdminAmcRate response=new ResponseListAdminAmcRate();
	@PostMapping("/addAdminAmc")
	public ResponseEntity<?> addRentRequest(@RequestBody AdminAmcRate amc) {
		ResponseAdminAmcDto response=new ResponseAdminAmcDto();
		try {
			if (adminService.addAdminamc(amc) != null) {
				response.setAdminAmcRates(adminService.addAdminamc(amc));
				response.setMessage("AMC added successfully..");
				response.setStatus(true);
				return ResponseEntity.ok(response);
			}
			response.setMessage("Invalid User.!");
			response.setStatus(false);
			return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
		}
	}
	
	@GetMapping("/getAdminAmcRates")
	public ResponseEntity<?> getAssetDetails(@RequestBody AdminAmcRate getAmc) {
		try {
			List<AdminAmcRate> entity = adminService.getAmcRates(getAmc);
			if (entity.isEmpty()) {
				response.setMessage("Invalid User.!");
				response.setStatus(false);
				return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
			}
			response.setMessage("Amc details fetched successfully.");
			response.setStatus(true);
			response.setData(entity);
			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (Exception e) {
			response.setMessage(e.getMessage());
			response.setStatus(false);
			return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
		}

	}

	@PutMapping("/updateAdminAmcRates")
	public ResponseEntity<ResponseAdminAmcDto> updateAssetDetails(@RequestBody AdminAmcRate updateAmc) {
		ResponseAdminAmcDto response=new ResponseAdminAmcDto();
		try {

			if (adminService.updateAmcRates(updateAmc) != null) {
				
				response.setAdminAmcRates(adminService.updateAmcRates(updateAmc));
				response.setMessage("Admin Amc Rates updated successfully..");
				response.setStatus(true);
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
			response.setMessage("Invalid User.!");
			response.setStatus(false);
			return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
		} catch (Exception e) {
			response.setMessage(e.getMessage());
			response.setStatus(false);
			return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
		}
	}
}
