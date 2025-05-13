package com.application.mrmason.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.application.mrmason.dto.QuotationDTO;
import com.application.mrmason.dto.ResponseQuotationtDTO;
import com.application.mrmason.entity.QuotationEntity;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.service.QuotationService;
import com.application.mrmason.service.impl.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class QuotationController {

	@Autowired
	private QuotationService quotationService;
	
	@Autowired
	UserService userService;

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ResponseQuotationtDTO> handleAccessDeniedException(AccessDeniedException ex) {
		ResponseQuotationtDTO response = new ResponseQuotationtDTO();
		response.setMessage("Access Denied");
		response.setStatus(false);
		log.warn("Access denied: {}", ex.getMessage());
		return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
	}

	@PostMapping("/add-quotation")
	public ResponseEntity<ResponseQuotationtDTO> createQuotation(@RequestBody QuotationEntity entity,@RequestParam RegSource regSource) {
		ResponseQuotationtDTO response = new ResponseQuotationtDTO();
		try {
			QuotationEntity savedquotationRequest = quotationService.createQuotation(entity,regSource);
			if (savedquotationRequest != null) {
				response.setMessage("QuotationRequest added successfully");
				response.setStatus(true);
				response.setData(mapToDTO(savedquotationRequest));
				return ResponseEntity.ok(response);
			}
			response.setMessage("Failed to add site quotationRequest");
			response.setStatus(false);
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			log.error("Error adding site quotationRequest: {}", e.getMessage());
			response.setMessage(e.getMessage());
			response.setStatus(false);
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private QuotationDTO mapToDTO(QuotationEntity quotationRequest) {
		QuotationDTO dto = new QuotationDTO();
		dto.setCustomerId(quotationRequest.getCustomerId());
		dto.setReqId(quotationRequest.getReqId());
		dto.setServicePersonId(quotationRequest.getServicePersonId());
		dto.setStatus(quotationRequest.getStatus());
		dto.setQuotedAmount(quotationRequest.getQuotedAmount());
		dto.setUnit(quotationRequest.getUnit());
		dto.setUpdatedDate(quotationRequest.getUpdatedDate());
		dto.setUpdatedBy(quotationRequest.getUpdatedBy());
		return dto;
	}
	
	@PutMapping("/update-quotation")
	public ResponseEntity<ResponseQuotationtDTO> updateQuotation(@RequestBody QuotationEntity entity,@RequestParam RegSource regSource) {
		ResponseQuotationtDTO response = new ResponseQuotationtDTO();
		try {
			QuotationEntity savedquotationRequest = quotationService.updateQuotation(entity,regSource);
			if (savedquotationRequest != null) {
				response.setMessage("QuotationRequest Updated successfully");
				response.setStatus(true);
				response.setData(mapToDTO(savedquotationRequest));
				return ResponseEntity.ok(response);
			}
			response.setMessage("Failed to update site quotationRequest");
			response.setStatus(false);
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			log.error("Error updating site quotationRequest: {}", e.getMessage());
			response.setMessage(e.getMessage());
			response.setStatus(false);
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/get-quotation")
    public ResponseEntity<GenericResponse<List<QuotationEntity>>> getQuotations(
            @RequestParam(required = false) String reqId,
            @RequestParam(required = false) String customerId,
            @RequestParam(required = false) String servicePersonId,
            @RequestParam(required = false) String updatedBy
    ) {
        List<QuotationEntity> quotations = quotationService.getQuotation(reqId, customerId, servicePersonId, updatedBy);

        GenericResponse<List<QuotationEntity>> response = new GenericResponse<>(
            "Quotation data fetched successfully",
            true,
            quotations
        );

        return ResponseEntity.ok(response);
    }
	
	

}