package com.application.mrmason.service;

import java.nio.file.AccessDeniedException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.application.mrmason.dto.ResponseModel;
import com.application.mrmason.entity.AdminMachineAssetsImages;

public interface AdminMachineAssetsImagesService {
	public AdminMachineAssetsImages addMachineAssetsImage(AdminMachineAssetsImages adminMachineAssetsImage);

	public AdminMachineAssetsImages updateMachineAssetsImage(AdminMachineAssetsImages adminMachineAssetsImage);

	public ResponseEntity<ResponseModel> uploadProfileImage(String categoryMachineBrandModel, MultipartFile imageUrl)
			throws AccessDeniedException;

	public Page<AdminMachineAssetsImages> get(String categoryMachineBrandModel, String category,String machineId, String brand, String modelId, String modelName, String subCategory,
			Pageable pageable);

}
