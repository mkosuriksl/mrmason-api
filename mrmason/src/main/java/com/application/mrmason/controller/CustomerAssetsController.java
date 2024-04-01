package com.application.mrmason.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.application.mrmason.dto.CustomerAssetDto;
import com.application.mrmason.dto.ResponseAssetDto;
import com.application.mrmason.dto.UpdateAssetDto;
import com.application.mrmason.entity.CustomerAssets;
import com.application.mrmason.service.CustomerAssetsService;

@RestController
public class CustomerAssetsController {
	@Autowired
	CustomerAssetsService assetService;

	@PostMapping("/addAssets")
	public ResponseEntity<?> newCustomer(@RequestBody CustomerAssets asset) {
		try {
			if (assetService.saveAssets(asset) != null) {
				assetService.saveAssets(asset);
				ResponseAssetDto response = new ResponseAssetDto();
				response.setAddAsset(assetService.getAssetByAssetId(asset.getAssetId()));
				response.setMessage("Asset added successfully..");
				return ResponseEntity.ok(response);
			}
			return new ResponseEntity<>("Invalid User.!", HttpStatus.UNAUTHORIZED);
		} catch (Exception e) {
			e.getMessage();
			return new ResponseEntity<>("Invalid User.!", HttpStatus.UNAUTHORIZED);
		}

	}

	@PutMapping("/updateAssets")
	public ResponseEntity<?> updateAssetDetails(@RequestBody CustomerAssetDto updateAsset) {
		try {

			if (assetService.updateAssets(updateAsset) != null) {
				assetService.updateAssets(updateAsset);
				ResponseAssetDto response = new ResponseAssetDto();
				response.setAddAsset(assetService.getAssetByAssetId(updateAsset.getAssetId()));
				response.setMessage("Asset updated successfully..");
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
			return new ResponseEntity<>("Invalid User.!", HttpStatus.UNAUTHORIZED);
		} catch (Exception e) {

			return new ResponseEntity<>("Invalid User.!", HttpStatus.UNAUTHORIZED);
		}
	}

	@GetMapping("/getAssets")
	public ResponseEntity<?> getAssetDetails(@RequestBody UpdateAssetDto getDto) {
		try {
			List<CustomerAssets> entity = assetService.getAssets(getDto);
			if (entity.isEmpty()) {
				return new ResponseEntity<>("Invalid User.!", HttpStatus.UNAUTHORIZED);
			}
			return new ResponseEntity<List<CustomerAssets>>(entity, HttpStatus.OK);

		} catch (Exception e) {
			e.getMessage();
			return new ResponseEntity<>("Invalid User.!", HttpStatus.UNAUTHORIZED);
		}

	}
}
