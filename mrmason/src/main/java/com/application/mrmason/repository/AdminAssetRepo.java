package com.application.mrmason.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.application.mrmason.entity.AdminAsset;
@Repository
public interface AdminAssetRepo extends JpaRepository<AdminAsset, String>{
	
	List<AdminAsset> findByAssetIdOrderByAddedDateDesc(String assetid);
	List<AdminAsset> findByAssetModelOrderByAddedDateDesc(String assetModel);
	
	List<AdminAsset> findByAssetCatOrderByAddedDateDesc(String assetCat);
	List<AdminAsset> findByAssetCatNotOrderByAddedDateDesc(String assetCat);
	
	List<AdminAsset> findByAssetSubCatOrderByAddedDateDesc(String assetSubCat);
	
	List<AdminAsset> findByAssetBrandOrderByAddedDateDesc(String assetBrand);
	AdminAsset findByAssetId(String assetId);
//	List<AdminAsset> findByAssetCatOrderByCreateDateDesc(String assetCat);
//	List<AdminAsset> findByAssetCatNotOrderByCreateDateDesc(String assetCat);
	
	 List<AdminAsset> findByAssetCat(String assetCat);
	Page<AdminAsset> findByAssetId(String assetId, Pageable pageable);
	Page<AdminAsset> findByAssetCat(String assetCat, Pageable pageable);
	Page<AdminAsset> findByAssetSubCat(String assetSubCat, Pageable pageable);
	Page<AdminAsset> findByAssetModel(String assetModel, Pageable pageable);
	Page<AdminAsset> findByAssetBrand(String assetBrand, Pageable pageable);
}
