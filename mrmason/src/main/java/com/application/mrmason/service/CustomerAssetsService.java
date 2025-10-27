package com.application.mrmason.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.multipart.MultipartFile;

import com.application.mrmason.dto.CustomerAssetDto;
import com.application.mrmason.dto.ResponseModel;
import com.application.mrmason.entity.CustomerAssets;
import com.application.mrmason.enums.RegSource;


public interface CustomerAssetsService {
	CustomerAssets saveAssets(CustomerAssets asset);
//	CustomerAssets updateAssets(CustomerAssetDto asset);
//	List<CustomerAssets> getAssets(String userId,String assetId,String location,String assetCat,String assetSubCat,String assetModel,String assetBrand);
//	public Page<CustomerAssets> getCustomerAssets(String userId, String assetId, String location, String assetCat,
//			String assetSubCat, String assetModel, String assetBrand, Pageable pageable) ;
//	CustomerAssetDto getAssetByAssetId(String assetId);
	

//	List<CustomerAssets> getAssets(String userId,String assetId,String location,String assetCat,String assetSubCat,String assetModel,String assetBrand);

	CustomerAssets updateAssets(CustomerAssetDto asset,RegSource regSource);

	CustomerAssetDto getAssetByAssetId(CustomerAssets asset, RegSource regSource);
	
	public Page<?> getAssets(String userId, String assetId, String location, String assetCat, String assetSubCat,
			String assetModel, String assetBrand, Pageable pageable, RegSource regSource) ;
	
	public ResponseEntity<ResponseModel> uploadprofileimage(String assetId, MultipartFile image, RegSource regSource)
			throws AccessDeniedException ;

}
