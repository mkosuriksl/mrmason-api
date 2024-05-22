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
import com.application.mrmason.dto.ResponseSpServiceDetailsDto;
import com.application.mrmason.dto.ResponseSpServiceGetDto;
import com.application.mrmason.entity.SpServiceDetails;
import com.application.mrmason.service.SpServiceDetailsService;
@RestController
@PreAuthorize("hasAuthority('Developer')")
public class SpServiceDetailsController {
	@Autowired
	SpServiceDetailsService spService;
	ResponseSpServiceDetailsDto response = new ResponseSpServiceDetailsDto();
	ResponseSpServiceGetDto response2=new ResponseSpServiceGetDto();
	@PostMapping("/addSpService")
	public ResponseEntity<ResponseSpServiceDetailsDto> newAdminAsset(@RequestBody SpServiceDetails sevice) {
		try {
			ResponseSpServiceDetailsDto data=spService.addServiceRequest(sevice);
			if ( data!= null) {
				return ResponseEntity.ok(data);
			}
			response.setMessage("Invalid details.!");
			response.setStatus(false);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setMessage(e.getMessage());
			response.setStatus(false);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}

	@GetMapping("/getSpService")
	public ResponseEntity<ResponseSpServiceGetDto> getAssetDetails(@RequestBody SpServiceDetails getDto) {
		try {
			ResponseSpServiceGetDto entity = spService.getServiceRequest(getDto);
			if (entity!=null) {
				
				return new ResponseEntity<>(entity, HttpStatus.OK);
			}
			response2.setMessage("Invalid Details.!");
			response2.setStatus(false);
			return ResponseEntity.ok(response2);

		} catch (Exception e) {
			response.setMessage(e.getMessage());
			response.setStatus(false);
			return new ResponseEntity<>(response2, HttpStatus.OK);
		}

	}

	@PutMapping("/updateSpService")
	public ResponseEntity<ResponseSpServiceDetailsDto> updateAssetDetails(@RequestBody SpServiceDetails service) {
		try {
			ResponseSpServiceDetailsDto data=spService.updateServiceRequest(service);
			if (data != null) {
				
				return new ResponseEntity<>(data, HttpStatus.OK);
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
