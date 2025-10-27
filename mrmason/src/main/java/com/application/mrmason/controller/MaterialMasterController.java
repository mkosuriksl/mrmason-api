package com.application.mrmason.controller;

import java.nio.file.AccessDeniedException;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.application.mrmason.dto.GenericResponse;
import com.application.mrmason.dto.MaterialMasterRequestDto;
import com.application.mrmason.dto.ResponseGetMasterDto;
import com.application.mrmason.dto.ResponseGetMaterialMasterDto;
import com.application.mrmason.entity.MaterialMaster;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.service.MaterialHomeService;
import com.application.mrmason.service.MaterialMasterService;

@RestController
@RequestMapping("/api")
public class MaterialMasterController {

	@Autowired
	private MaterialMasterService materialMasterService;
	
	@Autowired
	private MaterialHomeService homeService;

	@PostMapping("/add-material-master")
	public ResponseEntity<GenericResponse<List<MaterialMaster>>> createMaterials(
			@RequestBody List<MaterialMasterRequestDto> dtos, @RequestParam RegSource regSource) {

		List<MaterialMaster> savedMaterials = materialMasterService.saveMaterials(dtos, regSource);

		GenericResponse<List<MaterialMaster>> response = new GenericResponse<>("Materials saved successfully", true,
				savedMaterials);

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@PutMapping("/update-material-master")
	public ResponseEntity<GenericResponse<List<MaterialMaster>>> updateMaterials(
			@RequestBody List<MaterialMasterRequestDto> dtos, @RequestParam RegSource regSource) {

		List<MaterialMaster> updatedMaterials = materialMasterService.updateMaterials(dtos, regSource);

		GenericResponse<List<MaterialMaster>> response = new GenericResponse<>("Materials updated successfully", true,
				updatedMaterials);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/get-material-master")
	public ResponseEntity<ResponseGetMaterialMasterDto> getTask(@RequestParam(required = false) String serviceCategory,
			@RequestParam(required = false) String productCategory,
			@RequestParam(required = false) String productSubCategory, @RequestParam(required = false) String brand,
			@RequestParam(required = false) String model,
			@RequestParam(required = false) String userIdSku,
			@RequestParam(required = false) String userId,
			@RequestParam(required = false) RegSource regSource,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size)
			throws AccessDeniedException {

		Pageable pageable = PageRequest.of(page, size);
		Page<MaterialMaster> srpqPage = materialMasterService.get(serviceCategory, productCategory, productSubCategory,
				brand, model, userIdSku,userId,regSource,pageable);
		ResponseGetMaterialMasterDto response = new ResponseGetMaterialMasterDto();

		response.setMessage("Material Master is retrieved successfully.");
		response.setStatus(true);
		response.setMaterialMasters(srpqPage.getContent());

		// Set pagination fields
		response.setCurrentPage(srpqPage.getNumber());
		response.setPageSize(srpqPage.getSize());
		response.setTotalElements(srpqPage.getTotalElements());
		response.setTotalPages(srpqPage.getTotalPages());

		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@GetMapping("/home-search-by-location")
    public ResponseEntity<GenericResponse<ResponseGetMasterDto>> getMaterials(
            @RequestParam String location,
            @RequestParam(required = false) String materialCategory,
            @RequestParam(required = false) String materialSubCategory,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        // Call service to get ResponseGetMasterDto with paged MaterialMasters
        ResponseGetMasterDto responseDto = homeService.getMaterialsWithPagination(
                location, materialCategory, materialSubCategory, brand, model, page, size
        );

        // Wrap in GenericResponse
        GenericResponse<ResponseGetMasterDto> response = new GenericResponse<>(
                "Material Master is retrieved successfully",
                true,
                responseDto
        );

        return ResponseEntity.ok(response);
    }
	
	@GetMapping("/distinct-location-by-ms")
	public ResponseEntity<GenericResponse<List<String>>> autoSearchLocation(
	        @RequestParam String location,
	        @RequestParam(required = false) String materialCategory,
	        @RequestParam(required = false) String materialSubCategory,
	        @RequestParam(required = false) String brand,
	        @RequestParam(required = false) String model) {

	    List<String> locations = homeService.autoSearchLocations(location, materialCategory, materialSubCategory, brand, model);

	    GenericResponse<List<String>> response;

	    if (locations.isEmpty()) {
	        response = new GenericResponse<>("No records matches", false, Collections.emptyList());
	    } else {
	        response = new GenericResponse<>("Locations retrieved successfully", true, locations);
	    }

	    return ResponseEntity.ok(response);
	}


}
