package com.application.mrmason.service;

import java.util.List;

import com.application.mrmason.dto.CustomerAssetDto;
import com.application.mrmason.dto.UpdateAssetDto;
import com.application.mrmason.entity.CustomerAssets;

public interface CustomerAssetsService {
	CustomerAssets saveAssets(CustomerAssets asset);
	CustomerAssets updateAssets(CustomerAssetDto asset);
	List<CustomerAssets> getAssets(UpdateAssetDto assetDto);
	CustomerAssetDto getAssetByAssetId(String assetId);
}
