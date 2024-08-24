package com.application.mrmason.controller;

import java.util.List;
//import org.hibernate.mapping.List;
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

import com.application.mrmason.dto.ResponseListServicePersonAssets;
import com.application.mrmason.dto.ResponseSPAssetDTO;
import com.application.mrmason.dto.ServicePersonAssetsDTO;
import com.application.mrmason.entity.ServicePersonAssetsEntity;
import com.application.mrmason.service.ServicePersonAssetsService;

@RestController
@PreAuthorize("hasAuthority('Developer')")

public class ServicePersonAssetsController {
	@Autowired
	ServicePersonAssetsService assetService;

	ResponseListServicePersonAssets response = new ResponseListServicePersonAssets();

	@PostMapping("/addSPAssets")
	public ResponseEntity<ResponseSPAssetDTO> newServicePerson(@RequestBody ServicePersonAssetsEntity asset) {
		ResponseSPAssetDTO response = new ResponseSPAssetDTO();
		try {

			if (assetService.saveAssets(asset) != null) {
				assetService.saveAssets(asset);
				response.setAddSPAsset(assetService.getAssetByAssetId(asset.getAssetId()));
				response.setMessage("Asset added successfully..");
				response.setStatus(true);
				return ResponseEntity.ok(response);
			}
			//response.setMessage("Invalid User.!");
			//response.setStatus(false);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setMessage(e.getMessage());
			response.setStatus(false);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}

	}

	@PutMapping("/updateSPAsset")
	public ResponseEntity<?> updateSPAsset(@RequestBody ServicePersonAssetsDTO updateSPAsset) {
		try {
			ResponseSPAssetDTO response = new ResponseSPAssetDTO();
			if (assetService.updateAssets(updateSPAsset) != null) {
				assetService.updateAssets(updateSPAsset);
				response.setAddSPAsset(assetService.getAssetByAssetId(updateSPAsset.getAssetId()));
				response.setMessage("Asset updated successfully..");
				response.setStatus(true);
				return new ResponseEntity<>(response, HttpStatus.OK);
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

	@GetMapping("/getSPAssets")
	public ResponseEntity<ResponseListServicePersonAssets> getAssetDetails(
			@RequestParam(required = false) String userId, @RequestParam(required = false) String assetId,
			@RequestParam(required = false) String location, @RequestParam(required = false) String assetCat,
			@RequestParam(required = false) String assetSubCat, @RequestParam(required = false) String assetModel,
			@RequestParam(required = false) String assetBrand) {
				
	
		try {
			List<ServicePersonAssetsEntity> entity = assetService.getAssets(userId, assetId, location, assetCat,
					assetSubCat, assetModel, assetBrand);
			if (!entity.isEmpty()) {
				response.setMessage("Assets fetched successfully.");
				response.setStatus(true);
				response.setData(entity);
				return new ResponseEntity<>(response, HttpStatus.OK);

			}
			response.setMessage("No data found for the given details.!");
			response.setStatus(true);
			response.setData(entity);
			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (Exception e) {
			response.setMessage(e.getMessage());
			response.setStatus(false);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}

	}

}
