package com.application.mrmason.service;

import java.util.List;

import com.application.mrmason.entity.AdminAssetCategory;

public interface AdminAssetCategoryService {
	public AdminAssetCategory addAssetsCat(AdminAssetCategory asset);
	public List<AdminAssetCategory> getAssetCategoryCivil(String assetCategory);
	public List<AdminAssetCategory> getAssetCategoryNonCivil(String assetCategory);
}
