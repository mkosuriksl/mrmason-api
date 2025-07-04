package com.application.mrmason.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.application.mrmason.entity.AdminAssetCategory;

public interface AdminAssetCategoryService {
	public AdminAssetCategory addAssetsCat(AdminAssetCategory asset);
//	public List<AdminAssetCategory> getAssetCategoryCivil(String assetCategory);
	public Page<AdminAssetCategory> getAssetCategoryCivil(String assetCategory, Pageable pageable);
	public List<AdminAssetCategory> getAssetCategoryNonCivil(String assetCategory);
}
