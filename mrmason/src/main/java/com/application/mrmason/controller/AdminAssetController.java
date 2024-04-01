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
import com.application.mrmason.dto.ResponseAdminAssets;
import com.application.mrmason.dto.UpdateAssetDto;
import com.application.mrmason.entity.AdminAsset;
import com.application.mrmason.service.AdminAssetService;

@RestController
public class AdminAssetController {
	@Autowired
	public AdminAssetService adminService;
	
	ResponseAdminAssets response = new ResponseAdminAssets();

	@PostMapping("/addAdminAssets")
	public ResponseEntity<?> newAdminAsset(@RequestBody AdminAsset asset) {
		try {
			if (adminService.addAdminAssets(asset) != null) {

				response.setAddAsset(adminService.addAdminAssets(asset));
				response.setMessage("Asset added successfully..");
				return ResponseEntity.ok(response);
			}
			return new ResponseEntity<>("Invalid User.!", HttpStatus.UNAUTHORIZED);
		} catch (Exception e) {

			return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
		}
	}

	@GetMapping("/getAdminAssets")
	public ResponseEntity<?> getAssetDetails(@RequestBody UpdateAssetDto getDto) {
		try {
			List<AdminAsset> entity = adminService.getAssets(getDto);
			if (entity.isEmpty()) {
				return new ResponseEntity<>("Invalid User.!", HttpStatus.UNAUTHORIZED);
			}
			return new ResponseEntity<List<AdminAsset>>(entity, HttpStatus.OK);

		} catch (Exception e) {

			return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
		}

	}

	@PutMapping("/updateAdminAssets")
	public ResponseEntity<?> updateAssetDetails(@RequestBody UpdateAssetDto updateAsset) {
		try {

			if (adminService.updateAssets(updateAsset) != null) {
				
				response.setAddAsset(adminService.updateAssets(updateAsset));
				response.setMessage("Admin asset updated successfully..");
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
			return new ResponseEntity<>("Invalid User.!", HttpStatus.UNAUTHORIZED);
		} catch (Exception e) {

			return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
		}
	}
}
