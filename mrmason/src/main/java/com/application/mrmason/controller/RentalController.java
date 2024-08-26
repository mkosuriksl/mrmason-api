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

//import com.application.mrmason.dto.RentalDto;
import com.application.mrmason.dto.ResponseListRentalDto;
import com.application.mrmason.dto.ResponseRentalDto;
import com.application.mrmason.dto.UpdateRentalChargeDto;
import com.application.mrmason.entity.Rental;
import com.application.mrmason.service.RentalService;

@RestController
@PreAuthorize("hasAuthority('EC')")
public class RentalController {
	@Autowired
	public RentalService rentService;
	ResponseListRentalDto response = new ResponseListRentalDto();

	@PostMapping("/addRentalData")
	public ResponseEntity<ResponseRentalDto> addRentRequest(@RequestBody Rental rent) {
		ResponseRentalDto response = new ResponseRentalDto();
		try {
			if (rentService.addRentalReq(rent) != null) {

				response.setAddRental(rentService.addRentalReq(rent));
				response.setMessage("Rental asset added successfully..");
				response.setStatus(true);
				return ResponseEntity.ok(response);
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

	@GetMapping("/getRentalData")
	public ResponseEntity<?> getRentRequest(@RequestParam(required = false) String assetId,
			@RequestParam(required = false) String userId) {
		try {
			if (rentService.getRentalReq(assetId, userId).isEmpty()) {
				response.setMessage("No data found for the given details.!");
				response.setStatus(true);
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
			response.setMessage("Rental data fetched successfully.");
			response.setStatus(true);
			response.setData(rentService.getRentalReq(assetId, userId));
			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (Exception e) {
			response.setMessage(e.getMessage());
			response.setStatus(false);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}

	@PutMapping("/updateRentalData")
	public ResponseEntity<?> updateRentRequest(@RequestBody Rental rent) {
		try {
			ResponseRentalDto response = new ResponseRentalDto();
			if (rentService.updateRentalReq(rent) != null) {
				response.setAddRental(rentService.updateRentalReq(rent));
				response.setMessage("Rental asset updated successfully..");
				response.setStatus(true);
				return ResponseEntity.ok(response);
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

	@GetMapping("/getRentalAssets")
	public ResponseEntity<ResponseRentalDto> getRentalAssets(
			@RequestParam String assetCat,
			@RequestParam String assetSubCat,
			@RequestParam(required = false) String assetBrand,
			@RequestParam(required = false) String assetModel,
			@RequestParam String userId) {

		List<Rental> rentalAssets = rentService.getRentalAssets(assetCat, assetSubCat, assetBrand, assetModel, userId);

		ResponseRentalDto response = new ResponseRentalDto();

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

	@PutMapping("/updateAssetRentalCharge")
	public ResponseEntity<ResponseRentalDto> updateAssetRentalCharge(
			@RequestBody UpdateRentalChargeDto updateRequest) {

		Rental updatedRental = rentService.updateRentalAssetCharge(
				updateRequest.getAssetId(),
				updateRequest.getUserId(),
				updateRequest.getIsAvailRent(),
				updateRequest.getAmountPerDay(),
				updateRequest.getAmountper30days(),
				updateRequest.getPickup(),
				updateRequest.getAvailableLocation(),
				updateRequest.getDelivery());

		ResponseRentalDto response = new ResponseRentalDto();
		if (updatedRental == null) {
			response.setMessage("Asset not found or update failed.");
			response.setStatus(false);
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}

		response.setMessage(" Updated Rental Asset Charge successfully.");
		response.setStatus(true);
		response.setAddRental(updatedRental);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
