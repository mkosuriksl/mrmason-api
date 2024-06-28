package com.application.mrmason.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.application.mrmason.dto.UpdateAssetDto;
import com.application.mrmason.entity.AdminAsset;
import com.application.mrmason.entity.AdminAssetCategory;
import com.application.mrmason.repository.AdminAssetRepo;
import com.application.mrmason.service.AdminAssetService;

@Service
public class AdminAssetServiceImpl implements AdminAssetService {

	@Autowired
	public AdminAssetRepo adminAssetRepo;

	@Override
	public AdminAsset addAdminAssets(AdminAsset asset) {

		return adminAssetRepo.save(asset);
	}


	@Override
	public List<AdminAsset> getAssets(String assetId, String assetCat, String assetSubCat, String assetModel, String assetBrand) {
	    if (assetId != null) {
	        return adminAssetRepo.findByAssetIdOrderByAddedDateDesc(assetId);
	    } else if (assetCat != null) {
	        return adminAssetRepo.findByAssetCatOrderByAddedDateDesc(assetCat);
	    } else if (assetSubCat != null) {
	        return adminAssetRepo.findByAssetSubCatOrderByAddedDateDesc(assetSubCat);
	    } else if (assetModel != null) {
	        return adminAssetRepo.findByAssetModelOrderByAddedDateDesc(assetModel);
	    } else if (assetBrand != null) {
	        return adminAssetRepo.findByAssetBrandOrderByAddedDateDesc(assetBrand);
	    }
		return null; 
	}
	
	
	@Override
	public List<AdminAsset> getAssetCivil(String assetCat) {

		if ("CIVIL".equalsIgnoreCase(assetCat)) {
            return adminAssetRepo.findByAssetCat(assetCat);
        } else {
            return List.of(); 
        }


	}

	@Override
	public List<AdminAsset> getAssetNonCivil(String assetCat) {

		if (!"CIVIL".equalsIgnoreCase(assetCat)) {
            return adminAssetRepo.findByAssetCat(assetCat);
        } else {
            return List.of(); 
        }
	}


	@Override
	public AdminAsset updateAssets(UpdateAssetDto asset) {
		String assetId=asset.getAssetId();
		String assetCat=asset.getAssetCat();
		String assetSubCat=asset.getAssetSubCat();
		String assetModel=asset.getAssetModel();
		String assetBrand=asset.getAssetBrand();
		Optional<AdminAsset>  adminAsset=Optional.of(adminAssetRepo.findByAssetId(assetId));
		if(adminAsset.isPresent()) {
			adminAsset.get().setAssetCat(assetCat);
			adminAsset.get().setAssetSubCat(assetSubCat);
			adminAsset.get().setAssetModel(assetModel);
			adminAsset.get().setAssetBrand(assetBrand);
//			adminAsset.get().setAssetId(assetBrand+"_"+assetSubCat);
			
			return adminAssetRepo.save(adminAsset.get());
		}
		return null;
	}

	
}
