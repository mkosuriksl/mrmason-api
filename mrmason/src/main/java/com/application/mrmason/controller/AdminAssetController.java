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

import com.application.mrmason.dto.ResponseAdminAssets;
import com.application.mrmason.dto.ResponseListAdminAssets;
import com.application.mrmason.dto.UpdateAssetDto;
import com.application.mrmason.entity.AdminAsset;
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

	@GetMapping("/getAdminAssets")
	public ResponseEntity<?> getAssetDetails(@RequestBody UpdateAssetDto getDto) {
		try {
			List<AdminAsset> entity = adminService.getAssets(getDto);
			if (entity.isEmpty()) {
				response2.setMessage("Invalid User.!");
				response2.setStatus(false);
				return new ResponseEntity<>(response2, HttpStatus.UNAUTHORIZED);
			}
			response2.setData(entity);
			response2.setMessage("Admin asset details fetched successfully..");
			response2.setStatus(true);
			return ResponseEntity.ok(response2);

		} catch (Exception e) {
			response.setMessage(e.getMessage());
			response.setStatus(false);
			return new ResponseEntity<>(response2, HttpStatus.UNAUTHORIZED);
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
			return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
		} catch (Exception e) {

			return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
		}
	}
}
