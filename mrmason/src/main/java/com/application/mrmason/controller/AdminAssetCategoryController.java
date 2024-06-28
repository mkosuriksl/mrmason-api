package com.application.mrmason.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.application.mrmason.dto.ResponseAdminAssetCatDto;
import com.application.mrmason.dto.ResponseListServiceCatDto;
import com.application.mrmason.entity.AdminAsset;
import com.application.mrmason.entity.AdminAssetCategory;
import com.application.mrmason.entity.ServiceCategory;
import com.application.mrmason.service.AdminAssetCategoryService;

@RestController
@PreAuthorize("hasAuthority('Adm')")
public class AdminAssetCategoryController {

	@Autowired
	AdminAssetCategoryService service;

	@PostMapping("/addAdminAssetCategory")
	public ResponseEntity<?> addedAdminAssetCat(@RequestBody AdminAssetCategory asset) {

		AdminAssetCategory categories = service.addAssetsCat(asset);
		RequestAdminAssetCatDto response = new RequestAdminAssetCatDto();
		try {
			if (service.addAssetsCat(asset) != null) {

				response.setMessage("admin asset category added");
				response.setAssetCategoryData(categories);
				response.setStatus(true);
				return ResponseEntity.ok(response);
			}

		} catch (Exception e) {
			response.setMessage("Record alredy exists");
			response.setStatus(false);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
		return null;
	}

	@GetMapping("/getAssetCategory/civil/{assetCategory}")
	public ResponseEntity<?> getAssetCategoryCivilDetails(@PathVariable String assetCategory) {
		ResponseAdminAssetCatDto response = new ResponseAdminAssetCatDto();
		try {
			List<AdminAssetCategory> assets = service.getAssetCategoryCivil(assetCategory);
			response.setMessage("Civil related admin asset category fetched successfully");
			response.setStatus(true);
			response.setData(assets);
			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (Exception e) {
			response.setMessage(e.getMessage());
			response.setStatus(false);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}

	}
	
	@GetMapping("/getAssetCategory/nonCivil/{assetCategory}")
	public ResponseEntity<?> getAssetCategoryNonCivil(@PathVariable String assetCategory) {
		ResponseAdminAssetCatDto response = new ResponseAdminAssetCatDto();
		try {
			List<AdminAssetCategory> asset = service.getAssetCategoryNonCivil(assetCategory);

			response.setMessage("Non-Civil related admin asset category fetched successfully");
			response.setStatus(true);
			response.setData(asset);
			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (Exception e) {
			response.setMessage(e.getMessage());
			response.setStatus(false);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}

	}

}
