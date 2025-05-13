package com.application.mrmason.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

import com.application.mrmason.dto.MaterialRequirementByRequestDTO;
import com.application.mrmason.dto.ResponseMaterialRequirementByRequestDTO;
import com.application.mrmason.dto.ResponsesGetMaterialRequirementByRequestDto;
import com.application.mrmason.entity.MaterialRequirementByRequest;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.service.MaterialRequirementByRequestService;
import com.application.mrmason.service.impl.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class MaterialRequirementByRequestController {

	@Autowired
	private MaterialRequirementByRequestService requestService;

	@Autowired
	UserService userService;

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ResponseMaterialRequirementByRequestDTO> handleAccessDeniedException(
			AccessDeniedException ex) {
		ResponseMaterialRequirementByRequestDTO response = new ResponseMaterialRequirementByRequestDTO();
		response.setMessage("Access Denied");
		response.setStatus(false);
		log.warn("Access denied: {}", ex.getMessage());
		return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
	}

	@PostMapping("/add-materialRequirementByRequest")
	public ResponseEntity<ResponseMaterialRequirementByRequestDTO> createMaterialRequirementByRequest(
			@RequestBody MaterialRequirementByRequest entity, @RequestParam RegSource regSource) {
		ResponseMaterialRequirementByRequestDTO response = new ResponseMaterialRequirementByRequestDTO();
		try {
			MaterialRequirementByRequest savedRequest = requestService.createMaterialRequirementByRequest(entity,
					regSource);
			if (savedRequest != null) {
				response.setMessage("MaterialRequirementByRequest added successfully");
				response.setStatus(true);
				response.setData(mapToDTO(savedRequest));
				return ResponseEntity.ok(response);
			}
			response.setMessage("Failed to add site MaterialRequirementByRequest");
			response.setStatus(false);
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			log.error("Error adding site MaterialRequirementByRequest: {}", e.getMessage());
			response.setMessage(e.getMessage());
			response.setStatus(false);
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private MaterialRequirementByRequestDTO mapToDTO(MaterialRequirementByRequest entity) {
		MaterialRequirementByRequestDTO dto = new MaterialRequirementByRequestDTO();

		dto.setReqIdLineId(entity.getReqIdLineId());
		dto.setMaterialCategory(entity.getMaterialCategory());
		dto.setBrand(entity.getBrand());
		dto.setItemName(entity.getItemName());
		dto.setShape(entity.getShape());
		dto.setModelName(entity.getModelName());
		dto.setModelCode(entity.getModelCode());
		dto.setSizeInInch(entity.getSizeInInch());
		dto.setLength(entity.getLength());
		dto.setLengthInUnit(entity.getLengthInUnit() != null ? entity.getLengthInUnit().toString() : null);
		dto.setWidth(entity.getWidth());
		dto.setWidthInUnit(entity.getWidthInUnit() != null ? entity.getWidthInUnit().toString() : null);
		dto.setThickness(entity.getThickness());
		dto.setThicknessInUnit(entity.getThicknessInUnit() != null ? entity.getThicknessInUnit().toString() : null);
		dto.setNoOfItems(entity.getNoOfItems());
		dto.setWeightInKgs(entity.getWeightInKgs());
		dto.setAmount(entity.getAmount());
		dto.setGst(entity.getGst());
		dto.setTotalAmount(entity.getTotalAmount());

		// Already included in your method
		dto.setReqId(entity.getReqId());
		dto.setSpId(entity.getSpId());
		dto.setStatus(entity.getStatus());
		dto.setUpdatedDate(entity.getUpdatedDate());
		dto.setUpdatedBy(entity.getUpdatedBy());

		return dto;
	}

	@PutMapping("/update-materialRequirementByRequest")
	public ResponseEntity<ResponseMaterialRequirementByRequestDTO> updateMaterialRequirementByRequest(
			@RequestBody MaterialRequirementByRequest entity, @RequestParam RegSource regSource) {
		ResponseMaterialRequirementByRequestDTO response = new ResponseMaterialRequirementByRequestDTO();
		try {
			MaterialRequirementByRequest savedquotationRequest = requestService
					.updateMaterialRequirementByRequest(entity, regSource);
			if (savedquotationRequest != null) {
				response.setMessage("MaterialRequirementByRequest Updated successfully");
				response.setStatus(true);
				response.setData(mapToDTO(savedquotationRequest));
				return ResponseEntity.ok(response);
			}
			response.setMessage("Failed to update site MaterialRequirementByRequest");
			response.setStatus(false);
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			log.error("Error updating site MaterialRequirementByRequest: {}", e.getMessage());
			response.setMessage(e.getMessage());
			response.setStatus(false);
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	@GetMapping("/getMaterialRequirementByRequest")
	public ResponseEntity<ResponsesGetMaterialRequirementByRequestDto> getWorkers(@RequestParam String materialCategory,
			@RequestParam String brand, @RequestParam String itemName,
			@RequestParam(required = false) String reqIdLineId, @RequestParam(required = false) String modelName,
			@RequestParam(required = false) String modelCode, @RequestParam(required = false) String reqId,
			@RequestParam(required = false) String spId, @RequestParam(required = false) String updatedBy,
			@RequestParam(required = false) String status, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,@RequestParam RegSource regSource) {

		Pageable pageable = PageRequest.of(page, size);
		Page<MaterialRequirementByRequest> materialPage = requestService.getMaterialRequirementByRequest
				(materialCategory, brand, itemName, reqIdLineId, modelName,modelCode,reqId,
						spId, updatedBy, status,pageable,regSource);

		ResponsesGetMaterialRequirementByRequestDto response = new ResponsesGetMaterialRequirementByRequestDto();
		response.setMessage("MaterialRequirementByRequest details retrieved successfully.");
		response.setStatus(true);
		response.setMaterialData(materialPage.getContent());
		response.setUserData(null); // or your actual userData

		// Set pagination fields
		response.setCurrentPage(materialPage.getNumber());
		response.setPageSize(materialPage.getSize());
		response.setTotalElements(materialPage.getTotalElements());
		response.setTotalPages(materialPage.getTotalPages());

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}