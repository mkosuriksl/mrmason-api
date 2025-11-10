package com.application.mrmason.controller;

import java.nio.file.AccessDeniedException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.application.mrmason.dto.GenericResponse;
import com.application.mrmason.dto.ResponseGetAdminMachineAssetsImagesDto;
import com.application.mrmason.dto.ResponseGetWorkOrderSRHeaderQuotationDto;
import com.application.mrmason.entity.AdminMachineAssetsImages;
import com.application.mrmason.service.AdminMachineAssetsImagesService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/admin-machine-assets")
@Validated
public class AdminMachineAssetsImagesController {

	@Autowired
	private AdminMachineAssetsImagesService service;

	@PostMapping("/add")
	public ResponseEntity<GenericResponse<AdminMachineAssetsImages>> addMachineAssetsImage(
			@Valid @RequestBody AdminMachineAssetsImages request) {

		AdminMachineAssetsImages saved = service.addMachineAssetsImage(request);

		GenericResponse<AdminMachineAssetsImages> response = new GenericResponse<>(
				"Machine asset image added successfully", true, saved);
		return ResponseEntity.ok(response);
	}

	@PutMapping("/update")
	public ResponseEntity<GenericResponse<AdminMachineAssetsImages>> updateMachineAssetsImage(
			@Valid @RequestBody AdminMachineAssetsImages request) {

		AdminMachineAssetsImages updated = service.updateMachineAssetsImage(request);
		return ResponseEntity.ok(new GenericResponse<>("Machine asset updated successfully", true, updated));
	}

	@PostMapping("upload_doc")
	public ResponseEntity<?> uploadCabDocs(@RequestParam("categoryMachineBrandModel") String categoryMachineBrandModel,
			@RequestParam(value = "imageUrl", required = false) MultipartFile imageUrl) throws AccessDeniedException {
		return service.uploadProfileImage(categoryMachineBrandModel, imageUrl);
	}

	@GetMapping("/get")
	public ResponseEntity<ResponseGetAdminMachineAssetsImagesDto> getHeaderWorkOrder(
			@RequestParam(required = false) String categoryMachineBrandModel,
			@RequestParam(required = false) String category, @RequestParam(required = false) String machineId,
			@RequestParam(required = false) String brand, @RequestParam(required = false) String modelId,
			@RequestParam(required = false) String modelName, @RequestParam(required = false) String subCategory,
			Pageable pageable) {

		Page<AdminMachineAssetsImages> pageResult = service.get(categoryMachineBrandModel, category, machineId, brand,
				modelId, modelName, subCategory, pageable);

		ResponseGetAdminMachineAssetsImagesDto response = new ResponseGetAdminMachineAssetsImagesDto();
		response.setMessage("get Machine Assets Retrieved Successfully");
		response.setStatus(true);
		response.setAdminMachineAssetsImages(pageResult.getContent());
		response.setCurrentPage(pageResult.getNumber());
		response.setPageSize(pageResult.getSize());
		response.setTotalElements(pageResult.getTotalElements());
		response.setTotalPages(pageResult.getTotalPages());

		return ResponseEntity.ok(response);
	}

}
