package com.application.mrmason.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.application.mrmason.dto.CustomerAssetDto;
import com.application.mrmason.entity.CustomerAssets;

public interface CustomerAssetsService {
	CustomerAssets saveAssets(CustomerAssets asset);
	CustomerAssets updateAssets(CustomerAssetDto asset);
//	List<CustomerAssets> getAssets(String userId,String assetId,String location,String assetCat,String assetSubCat,String assetModel,String assetBrand);
	public Page<CustomerAssets> getCustomerAssets(String userId, String assetId, String location, String assetCat,
			String assetSubCat, String assetModel, String assetBrand, Pageable pageable) ;
	CustomerAssetDto getAssetByAssetId(String assetId);
}
