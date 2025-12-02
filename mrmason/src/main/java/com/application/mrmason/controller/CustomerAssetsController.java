package com.application.mrmason.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.application.mrmason.dto.CustomerAssetDto;
import com.application.mrmason.dto.GenericResponse;
import com.application.mrmason.dto.ResponseCustomerAssetsDto;
import com.application.mrmason.dto.ResponseListCustomerAssets;
import com.application.mrmason.entity.CustomerAssets;
import com.application.mrmason.entity.UserType;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.service.CustomerAssetsService;

@RestController
@PreAuthorize("hasAuthority('EC')")
public class CustomerAssetsController {
	@Autowired
	CustomerAssetsService assetService;

	ResponseListCustomerAssets response = new ResponseListCustomerAssets();

	@PostMapping("/addAssets")
	public ResponseEntity<GenericResponse<CustomerAssetDto>> addAsset(@RequestBody CustomerAssets asset,
			@RequestParam RegSource regSource,@RequestParam UserType userType) {

			// Call service directly to get DTO
			CustomerAssetDto assetDto = assetService.getAssetByAssetId(asset, regSource,userType);

			return ResponseEntity.ok(new GenericResponse<>("Asset added successfully.", true, assetDto));
	}

	@PutMapping("/updateAssets")
	public ResponseEntity<?> updateAssetDetails(@RequestBody CustomerAssetDto updateAsset,
			@RequestParam RegSource regSource,@RequestParam UserType userType) {
		try {
			// Call service directly to get DTO
			CustomerAssetDto assetDto = assetService.updateAssets(updateAsset, regSource,userType);

			return ResponseEntity.ok(new GenericResponse<>("Asset added successfully.", true, assetDto));

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new GenericResponse<>("Error: " + e.getMessage(), false, null));
		}
	}

	@GetMapping("/getAssets")
	public ResponseEntity<ResponseCustomerAssetsDto> getAssets(
	        @RequestParam(required = false) String userId,
	        @RequestParam(required = false) String assetId,
	        @RequestParam(required = false) String location,
	        @RequestParam(required = false) String assetCat,
	        @RequestParam(required = false) String assetSubCat,
	        @RequestParam(required = false) String assetModel,
	        @RequestParam(required = false) String assetBrand,
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size,
	        @RequestParam RegSource regSource,
	        @RequestParam UserType userType
	        ) {

	    Pageable pageable = PageRequest.of(page, size);
	    Page<?> assetsPage = assetService.getAssets(userId, assetId, location, assetCat, assetSubCat, assetModel, assetBrand, pageable, regSource,userType);

	    ResponseCustomerAssetsDto response = new ResponseCustomerAssetsDto();
	    response.setMessage("Assets retrieved successfully.");
	    response.setStatus(true);
	    response.setAssets(assetsPage.getContent());
	    response.setCurrentPage(assetsPage.getNumber());
	    response.setPageSize(assetsPage.getSize());
	    response.setTotalElements(assetsPage.getTotalElements());
	    response.setTotalPages(assetsPage.getTotalPages());

	    return new ResponseEntity<>(response, HttpStatus.OK);
	}
//	public ResponseEntity<ResponseCustomerAssetsDto> getAssets(@RequestParam(required = false) String userId,
//			@RequestParam(required = false) String assetId, @RequestParam(required = false) String location,
//			@RequestParam(required = false) String assetCat, @RequestParam(required = false) String assetSubCat,
//			@RequestParam(required = false) String assetModel, @RequestParam(required = false) String assetBrand,
//			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
//
//		Pageable pageable = PageRequest.of(page, size);
//		Page<CustomerAssets> assetsPage = assetService.getCustomerAssets(userId, assetId, location, assetCat,
//				assetSubCat, assetModel, assetBrand, pageable);
//
//		ResponseCustomerAssetsDto response = new ResponseCustomerAssetsDto();
//		response.setMessage("Customer assets retrieved successfully.");
//		response.setStatus(true);
//		response.setAssets(assetsPage.getContent());
//		response.setCurrentPage(assetsPage.getNumber());
//		response.setPageSize(assetsPage.getSize());
//		response.setTotalElements(assetsPage.getTotalElements());
//		response.setTotalPages(assetsPage.getTotalPages());
//
//		return new ResponseEntity<>(response, HttpStatus.OK);
//	}

}
