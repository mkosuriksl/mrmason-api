package com.application.mrmason.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.application.mrmason.dto.RentalDto;
import com.application.mrmason.dto.ResponseRentalDto;
import com.application.mrmason.entity.Rentel;
import com.application.mrmason.service.RentelService;

@RestController
@PreAuthorize("hasAuthority('EC')")
public class RentelController {
	@Autowired
	public RentelService rentService;

	@PostMapping("/addRentalData")
	public ResponseEntity<?> addRentRequest(@RequestBody Rentel rent) {
		try {
			if (rentService.addRentalReq(rent) != null) {
				ResponseRentalDto response=new ResponseRentalDto();
				response.setAddRental(rentService.addRentalReq(rent));
				response.setMessage("Rental asset added successfully..");
				
				return ResponseEntity.ok(response);
			}
			return new ResponseEntity<>("Invalid User.!", HttpStatus.UNAUTHORIZED);
		} catch (Exception e) {
			e.getMessage();
			return new ResponseEntity<>("Invalid User.!", HttpStatus.UNAUTHORIZED);
		}
	}
	
	@GetMapping("/getRentalData")
	public ResponseEntity<?> getRentRequest(@RequestBody RentalDto rent) {
		try {
			if (rentService.getRentalReq(rent).isEmpty()) {
				return new ResponseEntity<>("Invalid User.!", HttpStatus.UNAUTHORIZED);
			}
			return new ResponseEntity<>(rentService.getRentalReq(rent), HttpStatus.OK);
			
		} catch (Exception e) {
			e.getMessage();
			return new ResponseEntity<>("Invalid User.!", HttpStatus.UNAUTHORIZED);
		}
	}
	
	@PutMapping("/updateRentalData")
	public ResponseEntity<?> updateRentRequest(@RequestBody Rentel rent) {
		try {
			if (rentService.updateRentalReq(rent) != null) {
				ResponseRentalDto response=new ResponseRentalDto();
				response.setAddRental(rentService.updateRentalReq(rent));
				response.setMessage("Rental asset updated successfully..");
				return ResponseEntity.ok(response);
			}
			return new ResponseEntity<>("Invalid User.!", HttpStatus.UNAUTHORIZED);
		} catch (Exception e) {
			e.getMessage();
			return new ResponseEntity<>("Invalid User.!", HttpStatus.UNAUTHORIZED);
		}
	}
	
}
