package com.application.mrmason.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.application.mrmason.dto.ResponseSPAvailabilityDto;
import com.application.mrmason.dto.ResponseSPAvailabilityUpdateDto;
import com.application.mrmason.dto.UpdateAvailableDto;
import com.application.mrmason.entity.SPAvailability;
import com.application.mrmason.service.impl.SPAvailabilityServiceIml;

@RestController
@PreAuthorize("hasAuthority('Developer')")
public class SPAvailabilityController {

	@Autowired
	SPAvailabilityServiceIml spAvailableService;

	ResponseSPAvailabilityDto response=new ResponseSPAvailabilityDto();
	ResponseSPAvailabilityUpdateDto response2= new ResponseSPAvailabilityUpdateDto();
	@PostMapping("/sp-update-avalability")
	public ResponseEntity<?> updateAvailabilityOfAddress(@RequestBody SPAvailability available) {
		try {

			SPAvailability availability = spAvailableService.availability(available);

			if (availability != null) {
				response2.setMessage("Address updated successfully");
				response2.setStatus(true);
				response2.setGetData(availability);
				return new ResponseEntity<>(response2, HttpStatus.OK);
			}
			response2.setMessage("Invalid user");
			response2.setStatus(false);
			return ResponseEntity.status(HttpStatus.OK).body("response");

		} catch (Exception e) {
			response2.setMessage(e.getMessage());
			response2.setStatus(false);
			return new ResponseEntity<>(response2, HttpStatus.OK);
		}
		
	}

//	@GetMapping("/sp-get-update-availability")
//	public ResponseEntity<?> getAddress(@RequestParam String bodSeqNo) {
//		
//		try {
//			List<SPAvailability> availability = spAvailableService.getAvailability(bodSeqNo);
//			if (availability==null) {
//				response.setMessage("No data found for the given details.!");
//				response.setStatus(true);
//				response.setGetData(availability);
//				return new ResponseEntity<>(response, HttpStatus.OK);
//				
//			} else {
//				response.setMessage("Availability details.");
//				response.setGetData(availability);
//				response.setStatus(true);
//				return new ResponseEntity<>(response, HttpStatus.OK);
//			}
//		} catch (Exception e) {
//			response.setMessage(e.getMessage());
//			response.setStatus(false);
//			return new ResponseEntity<>(response, HttpStatus.OK);
//		}
//
//	}
	
	@GetMapping("/sp-get-update-availability")
	public ResponseEntity<?> getAddress(
	        @RequestParam String bodSeqNo,
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size) {

	    try {
	        Page<SPAvailability> availabilityPage = spAvailableService.getAvailability(bodSeqNo, page, size);

	        if (availabilityPage.isEmpty()) {
	            response.setMessage("No data found for the given details.!");
	            response.setStatus(true);
	            response.setGetData(Collections.emptyList());
	        } else {
	            response.setMessage("Availability details.");
	            response.setStatus(true);
	            response.setGetData(availabilityPage.getContent());
	        }

	        // Optional: include pagination metadata
	        response.setCurrentPage(page);
	        response.setPageSize(size);
	        response.setTotalElements(availabilityPage.getTotalElements());
	        response.setTotalPages(availabilityPage.getTotalPages());

	        return new ResponseEntity<>(response, HttpStatus.OK);

	    } catch (Exception e) {
	        response.setMessage(e.getMessage());
	        response.setStatus(false);
	        return new ResponseEntity<>(response, HttpStatus.OK);
	    }
	}

}