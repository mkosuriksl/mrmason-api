package com.application.mrmason.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.application.mrmason.dto.ResponseAdminAssetCatDto;
import com.application.mrmason.entity.AdminAssetCategory;
import com.application.mrmason.entity.ResponseList;
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

//	@GetMapping("/getAssetCategory/civil/{assetCategory}")
//	public ResponseEntity<?> getAssetCategoryCivilDetails(@PathVariable String assetCategory) {
//		ResponseAdminAssetCatDto response = new ResponseAdminAssetCatDto();
//		try {
//			List<AdminAssetCategory> assets = service.getAssetCategoryCivil(assetCategory);
//			response.setMessage("Civil related admin asset category fetched successfully");
//			response.setStatus(true);
//			response.setData(assets);
//			return new ResponseEntity<>(response, HttpStatus.OK);
//
//		} catch (Exception e) {
//			response.setMessage(e.getMessage());
//			response.setStatus(false);
//			return new ResponseEntity<>(response, HttpStatus.OK);
//		}
//
//	}
//	
	
	@GetMapping("/getAssetCategory/civil/{assetCategory}")
	public ResponseEntity<ResponseList<AdminAssetCategory>> getAssetCategoryCivilDetails(
	        @PathVariable String assetCategory,
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size) {

	    try {
	        Pageable pageable = PageRequest.of(page, size, Sort.by("createDate").descending());
	        Page<AdminAssetCategory> assetsPage = service.getAssetCategoryCivil(assetCategory, pageable);

	        ResponseList<AdminAssetCategory> response = new ResponseList<>();
	        response.setMessage("Civil-related admin asset category fetched successfully.");
	        response.setStatus(true);
	        response.setData(assetsPage.getContent());
	        response.setCurrentPage(assetsPage.getNumber());
	        response.setPageSize(assetsPage.getSize());
	        response.setTotalElements(assetsPage.getTotalElements());
	        response.setTotalPages(assetsPage.getTotalPages());

	        return new ResponseEntity<>(response, HttpStatus.OK);

	    } catch (Exception e) {
	        ResponseList<AdminAssetCategory> response = new ResponseList<>();
	        response.setMessage(e.getMessage());
	        response.setStatus(false);
	        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}

//	@GetMapping("/getAssetCategory/nonCivil/{assetCategory}")
//	public ResponseEntity<?> getAssetCategoryNonCivil(@PathVariable String assetCategory) {
//		ResponseAdminAssetCatDto response = new ResponseAdminAssetCatDto();
//		try {
//			List<AdminAssetCategory> asset = service.getAssetCategoryNonCivil(assetCategory);
//
//			response.setMessage("Non-Civil related admin asset category fetched successfully");
//			response.setStatus(true);
//			response.setData(asset);
//			return new ResponseEntity<>(response, HttpStatus.OK);
//
//		} catch (Exception e) {
//			response.setMessage(e.getMessage());
//			response.setStatus(false);
//			return new ResponseEntity<>(response, HttpStatus.OK);
//		}
//
//	}

	@GetMapping("/getAssetCategory/nonCivil/{assetCategory}")
	public ResponseEntity<ResponseList<AdminAssetCategory>> getAssetCategoryNonCivil(
	        @PathVariable String assetCategory,
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size) {

	    try {
	        Pageable pageable = PageRequest.of(page, size, Sort.by("createDate").descending());
	        Page<AdminAssetCategory> assetPage = service.getAssetCategoryNonCivil(assetCategory, pageable);

	        ResponseList<AdminAssetCategory> response = new ResponseList<>();
	        response.setMessage("Non-Civil related admin asset category fetched successfully.");
	        response.setStatus(true);
	        response.setData(assetPage.getContent());
	        response.setCurrentPage(assetPage.getNumber());
	        response.setPageSize(assetPage.getSize());
	        response.setTotalElements(assetPage.getTotalElements());
	        response.setTotalPages(assetPage.getTotalPages());

	        return new ResponseEntity<>(response, HttpStatus.OK);

	    } catch (Exception e) {
	        ResponseList<AdminAssetCategory> response = new ResponseList<>();
	        response.setMessage(e.getMessage());
	        response.setStatus(false);
	        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}

}
