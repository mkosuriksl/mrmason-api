package com.application.mrmason.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.application.mrmason.dto.RentalAssetResponseDTO;
import com.application.mrmason.dto.ResponseListSPRentalDTO;

import com.application.mrmason.dto.ResponseSPRentalDTO;
import com.application.mrmason.entity.ServicePersonRentalEntity;
import com.application.mrmason.service.ServicePersonRentalService;

@RestController
@PreAuthorize("hasAuthority('Developer')")

public class ServicePersonRentalController {

	@Autowired
	public ServicePersonRentalService spRentService;
	ResponseListSPRentalDTO response = new ResponseListSPRentalDTO();

	@PostMapping("/addServicePersonRentalData")
	public ResponseEntity<ResponseSPRentalDTO> addRentRequest(@RequestBody ServicePersonRentalEntity rent) {
		ResponseSPRentalDTO response = new ResponseSPRentalDTO();
		try {
			if (spRentService.addRentalReq(rent) != null) {

				response.setAddRental(spRentService.addRentalReq(rent));
				response.setMessage(" Service Person Rental asset added successfully..");
				response.setStatus(true);
				return ResponseEntity.ok(response);
			}
			response.setMessage(" Failed to add rental asset ..!");
			response.setStatus(false);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setMessage(e.getMessage());
			response.setStatus(false);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}

	@GetMapping("/getServicePersonRentalData")
	public ResponseEntity<ResponseSPRentalDTO> getRentalReq(
			@RequestParam(required = false) String assetCat,
			@RequestParam(required = false) String assetSubCat,
			@RequestParam(required = false) String assetBrand,
			@RequestParam(required = false) String assetModel,
			@RequestParam(required = false) String userId,
			@RequestParam(required = false) String assetId,
			@RequestParam(required = false) String availableLocation) {

		assetCat = (assetCat != null && assetCat.trim().isEmpty()) ? null : assetCat;
		assetSubCat = (assetSubCat != null && assetSubCat.trim().isEmpty()) ? null : assetSubCat;
		assetBrand = (assetBrand != null && assetBrand.trim().isEmpty()) ? null : assetBrand;
		assetModel = (assetModel != null && assetModel.trim().isEmpty()) ? null : assetModel;
		userId = (userId != null && userId.trim().isEmpty()) ? null : userId;
		assetId = (assetId != null && assetId.trim().isEmpty()) ? null : assetId;
		availableLocation = (availableLocation != null && availableLocation.trim().isEmpty()) ? null
				: availableLocation;

		List<RentalAssetResponseDTO> rentalAssets = spRentService.getRentalReq(assetCat, assetSubCat, assetBrand,
				assetModel, userId, assetId, availableLocation);

		ResponseSPRentalDTO response = new ResponseSPRentalDTO();

		if (rentalAssets.isEmpty()) {
			response.setMessage("No assets found for the given criteria.");
			response.setStatus(false);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}

		response.setMessage("Rental AssetIds retrieved successfully.");
		response.setStatus(true);
		response.setRentalData(rentalAssets);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PutMapping("/updateServicePersonAssetRentalCharge")
	public ResponseEntity<?> updateRentRequest(@RequestBody ServicePersonRentalEntity rent) {
		try {
			ResponseSPRentalDTO response = new ResponseSPRentalDTO();
			if (spRentService.updateRentalAssetCharge(rent) != null) {
				response.setAddRental(spRentService.updateRentalAssetCharge(rent));
				response.setMessage("Service Person Asset Rental Charge updated successfully..");
				response.setStatus(true);
				return ResponseEntity.ok(response);
			}
			response.setMessage("Asset not found or update failed !");
			response.setStatus(false);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setMessage(e.getMessage());
			response.setStatus(false);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}

	@GetMapping("/getServicePersonRentalAssets")
	public ResponseEntity<ResponseSPRentalDTO> getRentalAssets(
			@RequestParam(required = false) String assetCat,
			@RequestParam(required = false) String assetSubCat,
			@RequestParam(required = false) String assetBrand,
			@RequestParam(required = false) String assetModel,
			@RequestParam(required = false) String userId,
			@RequestParam(required = false) String assetId) {

		assetCat = (assetCat != null && assetCat.trim().isEmpty()) ? null : assetCat;
		assetSubCat = (assetSubCat != null && assetSubCat.trim().isEmpty()) ? null : assetSubCat;
		assetBrand = (assetBrand != null && assetBrand.trim().isEmpty()) ? null : assetBrand;
		assetModel = (assetModel != null && assetModel.trim().isEmpty()) ? null : assetModel;
		userId = (userId != null && userId.trim().isEmpty()) ? null : userId;
		assetId = (assetId != null && assetId.trim().isEmpty()) ? null : assetId;

		List<RentalAssetResponseDTO> rentalAssets = spRentService.getRentalAssets(assetCat, assetSubCat, assetBrand,
				assetModel, userId, assetId);

		ResponseSPRentalDTO response = new ResponseSPRentalDTO();

		if (rentalAssets.isEmpty()) {
			response.setMessage("No assets found for the given criteria.");
			response.setStatus(false);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}

		response.setMessage("Rental AssetIds retrieved successfully.");
		response.setStatus(true);
		response.setRentalData(rentalAssets);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
