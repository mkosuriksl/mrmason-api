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
import org.springframework.web.bind.annotation.RestController;

import com.application.mrmason.dto.CustomerAssetDto;
import com.application.mrmason.dto.ResponseAssetDto;
import com.application.mrmason.dto.ResponseListCustomerAssets;
import com.application.mrmason.dto.UpdateAssetDto;
import com.application.mrmason.entity.CustomerAssets;
import com.application.mrmason.service.CustomerAssetsService;


@RestController
@PreAuthorize("hasAuthority('EC')")
public class CustomerAssetsController {
	@Autowired
	CustomerAssetsService assetService;
	
	ResponseListCustomerAssets response=new ResponseListCustomerAssets();

	@PostMapping("/addAssets")
	public ResponseEntity<ResponseAssetDto> newCustomer(@RequestBody CustomerAssets asset) {
		ResponseAssetDto response = new ResponseAssetDto();
		try {
			
			if (assetService.saveAssets(asset) != null) {
				assetService.saveAssets(asset);
				response.setAddAsset(assetService.getAssetByAssetId(asset.getAssetId()));
				response.setMessage("Asset added successfully..");
				response.setStatus(true);
				return ResponseEntity.ok(response);
			}
			response.setMessage("Invalid User.!");
			response.setStatus(false);
			return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
		} catch (Exception e) {
			response.setMessage(e.getMessage());
			response.setStatus(false);
			return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
		}

	}

	@PutMapping("/updateAssets")
	public ResponseEntity<?> updateAssetDetails(@RequestBody CustomerAssetDto updateAsset) {
		try {
			ResponseAssetDto response = new ResponseAssetDto();
			if (assetService.updateAssets(updateAsset) != null) {
				assetService.updateAssets(updateAsset);
				response.setAddAsset(assetService.getAssetByAssetId(updateAsset.getAssetId()));
				response.setMessage("Asset updated successfully..");
				response.setStatus(true);
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
			response.setMessage("Invalid User.!");
			response.setStatus(false);
			return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
		} catch (Exception e) {
			response.setMessage(e.getMessage());
			response.setStatus(false);
			return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
		}
	}

	@GetMapping("/getAssets")
	public ResponseEntity<ResponseListCustomerAssets> getAssetDetails(@RequestBody UpdateAssetDto getDto) {
		try {
			List<CustomerAssets> entity = assetService.getAssets(getDto);
			if (entity.isEmpty()) {
				response.setMessage("Invalid User.!");
				response.setStatus(false);
				return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
			}
			response.setMessage("Assets fetched successfully.");
			response.setStatus(true);
			response.setData(entity);
			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (Exception e) {
			response.setMessage(e.getMessage());
			response.setStatus(false);
			return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
		}

	}
}
