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
import lombok.extern.slf4j.Slf4j;

import com.application.mrmason.dto.ResponseListServicePersonAssets;
import com.application.mrmason.dto.ResponseSPAssetDTO;
import com.application.mrmason.dto.ServicePersonAssetsDTO;
import com.application.mrmason.entity.ServicePersonAssetsEntity;
import com.application.mrmason.exceptions.ResourceNotFoundException;
import com.application.mrmason.service.ServicePersonAssetsService;

@RestController
@PreAuthorize("hasAuthority('Developer')")
@Slf4j
public class ServicePersonAssetsController {
	@Autowired
	ServicePersonAssetsService assetService;

	ResponseListServicePersonAssets response = new ResponseListServicePersonAssets();

	@PostMapping("/addSPAssets")
	public ResponseEntity<ResponseSPAssetDTO> newServicePerson(@RequestBody ServicePersonAssetsEntity asset) {
		ResponseSPAssetDTO response = new ResponseSPAssetDTO();
		try {
			ServicePersonAssetsEntity savedAsset = assetService.saveAssets(asset);
			if (savedAsset != null) {
				response.setAddSPAsset(assetService.getAssetByAssetId(savedAsset.getAssetId()));
				response.setMessage("Asset added successfully..");
				response.setStatus(true);
				log.info("Asset added: {}", savedAsset.getAssetId());
				return ResponseEntity.ok(response);
			}
			response.setMessage("Failed to add asset. Invalid user.");
			response.setStatus(false);
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			log.error("Error adding asset: {}", e.getMessage());
			response.setMessage(e.getMessage());
			response.setStatus(false);
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/updateSPAsset")
	public ResponseEntity<?> updateSPAsset(@RequestBody ServicePersonAssetsDTO updateSPAsset) {
		ResponseSPAssetDTO response = new ResponseSPAssetDTO();
		try {
			assetService.updateAssets(updateSPAsset);
			response.setAddSPAsset(assetService.getAssetByAssetId(updateSPAsset.getAssetId()));
			response.setMessage("Asset updated successfully..");
			response.setStatus(true);
			log.info("Asset updated: {}", updateSPAsset.getAssetId());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error updating asset: {}", e.getMessage());
			response.setMessage(e.getMessage());
			response.setStatus(false);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}

	@GetMapping("/getSPAssets")
	public ResponseEntity<ResponseListServicePersonAssets> getAssetDetails(@RequestParam String userId,
			@RequestParam(required = false) String assetId, @RequestParam(required = false) String location,
			@RequestParam(required = false) String assetCat, @RequestParam(required = false) String assetSubCat,
			@RequestParam(required = false) String assetModel, @RequestParam(required = false) String assetBrand) {

		if (userId == null || userId.isBlank() || userId.isEmpty()) {
			throw new ResourceNotFoundException("User Id is required.");
		}
		try {
			List<ServicePersonAssetsEntity> entity = assetService.getAssets(userId, assetId, location, assetCat,
					assetSubCat, assetModel, assetBrand);
			if (!entity.isEmpty()) {
				response.setMessage("Assets fetched successfully.");
				response.setStatus(true);
				response.setData(entity);
				log.info("Fetched {} assets for userId: {}", entity.size(), userId);
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
			response.setMessage("No data found for the given details.!");
			response.setStatus(true);
			response.setData(entity);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error fetching assets: {}", e.getMessage());
			response.setMessage(e.getMessage());
			response.setStatus(false);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}
}
