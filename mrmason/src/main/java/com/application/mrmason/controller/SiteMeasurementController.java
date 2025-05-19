package com.application.mrmason.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.application.mrmason.dto.GenericResponse;
import com.application.mrmason.dto.ResponseGetSiteMeasurementDTO;
import com.application.mrmason.dto.ResponseSiteMeasurementDTO;
import com.application.mrmason.dto.SiteMeasurementDTO;
import com.application.mrmason.dto.UpdateSiteMeasurementStatusRequestDTO;
import com.application.mrmason.dto.UpdateSiteMeasurementStatusResponseDTO;
import com.application.mrmason.entity.SiteMeasurement;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.service.SiteMeasurementService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class SiteMeasurementController {

	@Autowired
	private SiteMeasurementService siteMeasurementService;

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ResponseSiteMeasurementDTO> handleAccessDeniedException(AccessDeniedException ex) {
		ResponseSiteMeasurementDTO response = new ResponseSiteMeasurementDTO();
		response.setMessage("Access Denied");
		response.setStatus(false);
		log.warn("Access denied: {}", ex.getMessage());
		return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
	}

	@PostMapping("/add-site-measurement")
	public ResponseEntity<ResponseSiteMeasurementDTO> addSiteMeasurement(@RequestBody SiteMeasurement measurement,
			@RequestParam(required = false) RegSource regSource) {
		ResponseSiteMeasurementDTO response = new ResponseSiteMeasurementDTO();
		if (regSource == null) {
	        regSource = RegSource.MRMASON;
	    }
		try {
			SiteMeasurement savedMeasurement = siteMeasurementService.addSiteMeasurement(measurement, regSource);
			if (savedMeasurement != null) {
				response.setMessage("Site measurement added successfully");
				response.setStatus(true);
				response.setData(mapToDTO(savedMeasurement));
				log.info("Site measurement added for service request: {}", savedMeasurement.getServiceRequestId());
				return ResponseEntity.ok(response);
			}
			response.setMessage("Failed to add site measurement");
			response.setStatus(false);
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			log.error("Error adding site measurement: {}", e.getMessage());
			response.setMessage(e.getMessage());
			response.setStatus(false);
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/update-site-measurement")
	public ResponseEntity<ResponseSiteMeasurementDTO> updateSiteMeasurement(@RequestBody SiteMeasurement measurement,
			@RequestParam(required = false) RegSource regSource) {
		ResponseSiteMeasurementDTO response = new ResponseSiteMeasurementDTO();
		if (regSource == null) {
	        regSource = RegSource.MRMASON;
	    }
		try {
			SiteMeasurement updatedMeasurement = siteMeasurementService.updateSiteMeasurement(measurement, regSource);
			if (updatedMeasurement != null) {
				response.setMessage("Site measurement updated successfully");
				response.setStatus(true);
				response.setData(mapToDTO(updatedMeasurement));
				log.info("Site measurement updated for service request: {}", updatedMeasurement.getServiceRequestId());
				return ResponseEntity.ok(response);
			}
			response.setMessage("Invalid service request ID or measurement not found");
			response.setStatus(false);
			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			log.error("Error updating site measurement: {}", e.getMessage());
			response.setMessage(e.getMessage());
			response.setStatus(false);
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private SiteMeasurementDTO mapToDTO(SiteMeasurement measurement) {
		SiteMeasurementDTO dto = new SiteMeasurementDTO();
		dto.setServiceRequestId(measurement.getServiceRequestId());
		dto.setEastSiteLength(measurement.getEastSiteLength());
		dto.setWestSiteLength(measurement.getWestSiteLength());
		dto.setSouthSiteLength(measurement.getSouthSiteLength());
		dto.setNorthSiteLength(measurement.getNorthSiteLength());
		dto.setLocation(measurement.getLocation());
		dto.setExpectedBedRooms(measurement.getExpectedBedRooms());
		dto.setExpectedAttachedBathRooms(measurement.getExpectedAttachedBathRooms());
		dto.setExpectedAdditionalBathRooms(measurement.getExpectedAdditionalBathRooms());
		dto.setExpectedStartDate(measurement.getExpectedStartDate());
		dto.setUpdatedDate(measurement.getUpdatedDate());
		dto.setUpdatedBy(measurement.getUpdatedBy());
		dto.setCustomerId(measurement.getCustomerId());
		dto.setUserId(measurement.getUserId());
		dto.setBuildingType(measurement.getBuildingType());
		dto.setNoOfFloors(measurement.getNoOfFloors());
		dto.setRequestDate(measurement.getRequestDate());
		dto.setStatus(measurement.getStatus());
		return dto;
	}

	@GetMapping("/get-site-measurement")
	public ResponseEntity<?> getSiteMeasurement(@RequestParam(required = false) String serviceRequestId,
			@RequestParam(required = false) String eastSiteLegth, @RequestParam(required = false) String location,
			@RequestParam(required = false) String userId,
	        @RequestParam(required = false)@DateTimeFormat(pattern = "dd-MM-yyyy") Date fromRequestDate,
	        @RequestParam(required = false)@DateTimeFormat(pattern = "dd-MM-yyyy") Date toRequestDate, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {

		ResponseGetSiteMeasurementDTO response = new ResponseGetSiteMeasurementDTO();

		try {
			Pageable pageable = PageRequest.of(page, size);
			Page<SiteMeasurement> smPage = siteMeasurementService.getSiteMeasurement(serviceRequestId, eastSiteLegth,
					location, userId,fromRequestDate,toRequestDate, pageable);

			if (!smPage.isEmpty()) {
				response.setMessage("Site Measurement Details");
				response.setStatus(true);
				response.setData(smPage.getContent());
			} else {
				response.setMessage("No details found for given parameters/check your parameters");
				response.setStatus(false);
				response.setData(List.of());
			}

			// Optional: Add pagination metadata to response DTO
			response.setCurrentPage(smPage.getNumber());
			response.setPageSize(smPage.getSize());
			response.setTotalElements(smPage.getTotalElements());
			response.setTotalPages(smPage.getTotalPages());

			return ResponseEntity.ok(response);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}
	
	@PutMapping("/update-status")
    public ResponseEntity<GenericResponse<UpdateSiteMeasurementStatusResponseDTO>> updateStatus(
            @RequestBody UpdateSiteMeasurementStatusRequestDTO dto,
            @RequestParam(required = false) RegSource regSource) {
		if (regSource == null) {
	        regSource = RegSource.MRMASON;
	    }
        UpdateSiteMeasurementStatusResponseDTO response = siteMeasurementService.updateStatus(dto, regSource);

        GenericResponse<UpdateSiteMeasurementStatusResponseDTO> genericResponse = new GenericResponse<>(
                "Site measurement status updated successfully",
                true,
                response
        );

        return ResponseEntity.ok(genericResponse);
    }

//	@GetMapping("/get-site-measurement")
//	public ResponseEntity<?> getSiteMeasurement(@RequestParam(required = false) String serviceRequestId,
//			@RequestParam(required = false) String eastSiteLegth, @RequestParam(required = false) String location,
//			@RequestParam(required = false) Map<String, String> requestParams) {
//
//		ResponseGetSiteMeasurementDTO response = new ResponseGetSiteMeasurementDTO();
//
//		try {
//			List<SiteMeasurement> sm = siteMeasurementService.getSiteMeasurement(serviceRequestId, eastSiteLegth,
//					location);
//			if (sm != null && !sm.isEmpty()) {
//				response.setMessage("Site Measurement Details");
//				response.setStatus(true);
//				response.setData(sm);
//			} else {
//				response.setMessage("No details found for given parameters/check your parameters");
//				response.setStatus(false);
//				response.setData(sm != null ? sm : List.of());
//			}
//			return ResponseEntity.ok(response);
//		} catch (Exception e) {
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
//		}
//	}
}