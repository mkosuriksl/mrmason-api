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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.application.mrmason.dto.ResponseAdminServiceChargesDto;
import com.application.mrmason.dto.ResponseAdminServiceChargesDto1;
import com.application.mrmason.dto.ResponseAdminServiceChargesDto2;
import com.application.mrmason.entity.AdminServiceCharges;
import com.application.mrmason.service.AdminServiceChargesService;

@RestController
@PreAuthorize("hasAuthority('Adm')")
public class AdminServiceChargesController {

	@Autowired
	AdminServiceChargesService service;

	@PostMapping("/adding_AdminServiceCharges")
	public ResponseEntity<?> adminServiceCharges(@RequestBody AdminServiceCharges charges) {
		ResponseAdminServiceChargesDto response = new ResponseAdminServiceChargesDto();
		AdminServiceCharges admincharges = service.addCharges(charges);

		try {
			if (admincharges != null) {
				response.setMessage("added service charges");
				response.setStatus(true);
				response.setServiceChargeData(admincharges);
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
			response.setMessage("Record alredy exists");
			response.setStatus(false);
			response.setServiceChargeData(admincharges);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setMessage(e.getMessage());
			response.setStatus(false);
			response.setServiceChargeData(admincharges);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}

	}

	@GetMapping("/adminServiceCharegs")
	public ResponseEntity<?> getAdminCharges(@RequestParam(required = false) String serviceChargeKey,
			@RequestParam(required = false) String serviceId, @RequestParam(required = false) String location,
			@RequestParam(required = false) String brand, @RequestParam(required = false) String model) {
		
		ResponseAdminServiceChargesDto1 response = new ResponseAdminServiceChargesDto1();
		try {

			List<AdminServiceCharges> serviceCharges = service.getAdminServiceCharges(serviceChargeKey, serviceId, location, brand, model);
			if (serviceCharges != null && !serviceCharges.isEmpty()) {
				response.setMessage("Admin service charges details");
				response.setStatus(true);
				response.setGetData(serviceCharges);
				return new ResponseEntity<>(response, HttpStatus.OK);
			} else {
				response.setMessage("No details found for given parameters/check your parameters");
				response.setStatus(false);
				response.setGetData(serviceCharges);
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.OK).body(e.getMessage());
		}

	}
	
	
	@PutMapping("/updatingAdminServiceCharges")
	public ResponseEntity<?> updateAdminServiceCharges(@RequestBody AdminServiceCharges serviceCharge){
		ResponseAdminServiceChargesDto2 response = new ResponseAdminServiceChargesDto2();
		AdminServiceCharges services = service.updateCharges(serviceCharge);
		try {
			if(services!=null) {
				response.setMessage("service charges updated successfully");
				response.setStatus(true);
				response.setUpdatedData(services);
				return new ResponseEntity<>(response,HttpStatus.OK);
			}else {
				response.setMessage("failed to updated/servicechargekey not present");
				response.setStatus(true);
				response.setUpdatedData(services);
				return new ResponseEntity<>(response,HttpStatus.OK);
			}
		}catch(Exception e) {
			return ResponseEntity.status(HttpStatus.OK).body(e.getMessage());
		}
		
		
	}
	
}
