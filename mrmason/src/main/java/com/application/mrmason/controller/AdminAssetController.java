package com.application.mrmason.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.application.mrmason.dto.ResponseAdminAssetCatDto;
import com.application.mrmason.dto.ResponseAdminAssetDto;
import com.application.mrmason.dto.ResponseAdminAssets;
import com.application.mrmason.dto.ResponseListAdminAssets;
import com.application.mrmason.dto.UpdateAssetDto;
import com.application.mrmason.entity.AdminAsset;
import com.application.mrmason.entity.AdminAssetCategory;
import com.application.mrmason.entity.ResponseList;
import com.application.mrmason.service.AdminAssetService;

@RestController
@PreAuthorize("hasAuthority('Adm')")
public class AdminAssetController {
	@Autowired
	public AdminAssetService adminService;
	
	ResponseAdminAssets response = new ResponseAdminAssets();
	ResponseListAdminAssets response2=new ResponseListAdminAssets();
	@PostMapping("/addAdminAssets")
	public ResponseEntity<?> newAdminAsset(@RequestBody AdminAsset asset) {
		try {
			if (adminService.addAdminAssets(asset) != null) {

				response.setAddAsset(adminService.addAdminAssets(asset));
				response.setMessage("Asset Category added");
				response.setStatus(true);
				return ResponseEntity.ok(response);
			}
			response.setMessage("Invalid User.!");
			response.setStatus(false);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setMessage("Record alredy exists");
			response.setStatus(false);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}

//	@GetMapping("/getAdminAssets")
//	public ResponseEntity<?> getAssetDetails(
//	        @RequestParam(required = false) String assetId,
//	        @RequestParam(required = false) String assetCat,
//	        @RequestParam(required = false) String assetSubCat,
//	        @RequestParam(required = false) String assetModel,
//	        @RequestParam(required = false) String assetBrand) {
//	    try {
//	        List<AdminAsset> entity = adminService.getAssets(assetId, assetCat, assetSubCat, assetModel, assetBrand);
//	        if (entity.isEmpty()) {
//	            response2.setMessage("No data found for the given details.");
//	            response2.setStatus(true);
//	            response2.setData(entity);
//	            return new ResponseEntity<>(response2, HttpStatus.OK);
//	        }
//	        response2.setData(entity);
//	        response2.setMessage("Admin asset details fetched successfully.");
//	        response2.setStatus(true);
//	        return ResponseEntity.ok(response2);
//
//	    } catch (Exception e) {
//	        response2.setMessage(e.getMessage());
//	        response2.setStatus(false);
//	        return new ResponseEntity<>(response2, HttpStatus.OK);
//	    }
//	}

	@GetMapping("/getAdminAssets")
	public ResponseEntity<?> getAssetDetails(
	        @RequestParam(required = false) String assetId,
	        @RequestParam(required = false) String assetCat,
	        @RequestParam(required = false) String assetSubCat,
	        @RequestParam(required = false) String assetModel,
	        @RequestParam(required = false) String assetBrand,
	        @RequestParam(defaultValue = "0") int pageNo,
	        @RequestParam(defaultValue = "10") int pageSize) {
		try {
            Page<AdminAsset> entityPage = adminService.getAssets(
                    assetId, assetCat, assetSubCat, assetModel, assetBrand, pageNo, pageSize);

            ResponseList<AdminAsset> response = new ResponseList<>();
            response.setMessage("Admin asset details fetched successfully.");
            response.setData(entityPage.getContent());
            response.setCurrentPage(pageNo);
            response.setPageSize(pageSize);
            response.setTotalElements(entityPage.getTotalElements());
            response.setTotalPages(entityPage.getTotalPages());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", false);
            error.put("message", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
	}

	
	@GetMapping("/getAdminAsset/civil/{assetCat}")
	public ResponseEntity<?> getAdminAssetCivilDetails(@PathVariable String assetCat) {
		ResponseAdminAssetDto response = new ResponseAdminAssetDto();
		try {
			List<AdminAsset> assets = adminService.getAssetCivil(assetCat);
			response.setMessage("Civil related admin asset categories fetched successfully");
			response.setStatus(true);
			response.setData(assets);
			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (Exception e) {
			response.setMessage(e.getMessage());
			response.setStatus(false);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}

	}
	
	@GetMapping("/getAdminAsset/nonCivil/{assetCat}")
	public ResponseEntity<?> getAdminAssetNonCivil(@PathVariable String assetCat) {
		ResponseAdminAssetDto response = new ResponseAdminAssetDto();
		try {
			List<AdminAsset> asset = adminService.getAssetNonCivil(assetCat);

			response.setMessage("Non-Civil related admin asset categories fetched successfully");
			response.setStatus(true);
			response.setData(asset);
			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (Exception e) {
			response.setMessage(e.getMessage());
			response.setStatus(false);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}

	}
	
		
	@PutMapping("/updateAdminAssets")
	public ResponseEntity<?> updateAssetDetails(@RequestBody UpdateAssetDto updateAsset) {
		try {

			if (adminService.updateAssets(updateAsset) != null) {
				
				response.setAddAsset(adminService.updateAssets(updateAsset));
				response.setMessage("Admin asset updated successfully..");
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
}
