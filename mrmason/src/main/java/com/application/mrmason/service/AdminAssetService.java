package com.application.mrmason.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.application.mrmason.dto.UpdateAssetDto;
import com.application.mrmason.entity.AdminAsset;


public interface AdminAssetService {
	AdminAsset addAdminAssets(AdminAsset asset);
//	List<AdminAsset> getAssets(String assetId,String assetCat,String assetSubCat,String assetModel,String assetBrand);
	AdminAsset updateAssets(UpdateAssetDto asset);
	
//	public List<AdminAsset> getAssetCivil(String assetCat);
	public List<AdminAsset> getAssetNonCivil(String assetCat);
	public Page<AdminAsset> getAssetCivil(String assetCat, int pageNo, int pageSize);
	public Page<AdminAsset> getAssets(String assetId, String assetCat, String assetSubCat, String assetModel,
            String assetBrand, int pageNo, int pageSize);
}
