package com.application.mrmason.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.application.mrmason.entity.AdminAsset;
import com.application.mrmason.entity.AdminAssetCategory;
import com.application.mrmason.repository.AdminAssetCategoryRepo;
import com.application.mrmason.service.AdminAssetCategoryService;

@Service
public class AdminAssetCategoryServiceImpl implements AdminAssetCategoryService {
	
	@Autowired
	AdminAssetCategoryRepo repo;
	
	@Override
	public AdminAssetCategory addAssetsCat(AdminAssetCategory asset) {

		return repo.save(asset);
	}

	
//	@Override
//	public List<AdminAssetCategory> getAssetCategoryCivil(String assetCategory) {
//
//		List<AdminAssetCategory> assets = (repo.findByAssetCategoryOrderByCreateDateDesc(assetCategory));
//		return assets;
//
//	}
	
	@Override
	public Page<AdminAssetCategory> getAssetCategoryCivil(String assetCategory, Pageable pageable) {
	    return repo.findByAssetCategory(assetCategory, pageable);
	}


	@Override
	public List<AdminAssetCategory> getAssetCategoryNonCivil(String assetCategory) {

		List<AdminAssetCategory> assets = (repo.findByAssetCategoryNotOrderByCreateDateDesc(assetCategory));
		return assets;
	}

}
