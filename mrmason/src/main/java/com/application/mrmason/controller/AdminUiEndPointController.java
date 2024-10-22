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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.application.mrmason.dto.AdminUiEndPointDto;
import com.application.mrmason.dto.ResponseAdminUiEndPointDto;
import com.application.mrmason.entity.AdminUiEndPoint;
import com.application.mrmason.service.AdminUiEndPointService;

@RestController
public class AdminUiEndPointController {

	
	@Autowired
	private AdminUiEndPointService adminUiEndPointService;
	
	@PostMapping("/create-adminUiEndPoint")
	public ResponseEntity<AdminUiEndPointDto> createAdminUiEndPoint(
	        @RequestBody AdminUiEndPointDto adminUiEndPointDto) {
	    String adminName = adminUiEndPointDto.getUpdatedBy();
	    AdminUiEndPointDto createdAdminUiEndPointDto = adminUiEndPointService
	            .createAdminAcademicQualification(adminUiEndPointDto, adminName);
	    
	    return new ResponseEntity<>(createdAdminUiEndPointDto, HttpStatus.CREATED);
	}
	
	@GetMapping("/adminUiEndPoint-details")
	public ResponseEntity<?> getAdminUiEndPointDetails(@RequestParam(required = false) String systemId,
			@RequestParam(required = false) String ipUrlToUi, @RequestParam(required = false) String updatedBy) {

		ResponseAdminUiEndPointDto response = new ResponseAdminUiEndPointDto();
		try {
			List<AdminUiEndPoint> dleradminUiEndPoint= adminUiEndPointService.getAdminUiEndPointDto(systemId, ipUrlToUi,
					updatedBy);

			if (dleradminUiEndPoint != null || dleradminUiEndPoint.isEmpty()) {
				response.setMessage("dler store details");
				response.setStatus(true);
				response.setData(dleradminUiEndPoint);
				return new ResponseEntity<>(response, HttpStatus.OK);
			} else {
				response.setMessage("No details found for given parameters/check your parameters");
				response.setStatus(false);
				response.setData(dleradminUiEndPoint);
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.OK).body(e.getMessage());
		}
	}
	
	@PutMapping("/getadminUiEndPointById/{systemId}")
	public ResponseEntity<AdminUiEndPointDto>updatedAdminUiEndPoint(@RequestBody AdminUiEndPointDto adminUiEndPointDto,@PathVariable("systemId")String systemId){
		AdminUiEndPointDto updatedAdminUiEndPointDtoDto=adminUiEndPointService.updateAdminUiEndPoint(adminUiEndPointDto, systemId);
		return new ResponseEntity<AdminUiEndPointDto>(updatedAdminUiEndPointDtoDto,HttpStatus.OK);
	}

}
