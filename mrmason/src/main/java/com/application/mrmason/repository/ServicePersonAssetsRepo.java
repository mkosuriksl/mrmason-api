package com.application.mrmason.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.application.mrmason.entity.ServicePersonAssetsEntity;

public interface ServicePersonAssetsRepo extends JpaRepository<ServicePersonAssetsEntity, Long> {

	List<ServicePersonAssetsEntity> findByLocationOrderByIdDesc(String location);

	List<ServicePersonAssetsEntity> findByUserIdOrderByIdDesc(String userid);

	List<ServicePersonAssetsEntity> findByAssetIdOrderByIdDesc(String assetid);

	List<ServicePersonAssetsEntity> findByAssetModelOrderByIdDesc(String assetModel);

	List<ServicePersonAssetsEntity> findByAssetCatOrderByIdDesc(String assetCat);

	List<ServicePersonAssetsEntity> findByAssetSubCatOrderByIdDesc(String assetSubCat);

	List<ServicePersonAssetsEntity> findByAssetBrandOrderByIdDesc(String assetBrand);

	Optional<ServicePersonAssetsEntity> findAllByAssetId(String asssetid);

	Optional<ServicePersonAssetsEntity> findByUserIdAndAssetId(String userId, String assetId);

	List<ServicePersonAssetsEntity> findByAssetCatAndAssetSubCatAndAssetBrandAndAssetModelAndUserId(String assetCat,
			String assetSubCat,
			String assetBrand, String assetModel, String userId);

	List<ServicePersonAssetsEntity> findByAssetCatAndAssetSubCatAndAssetBrandAndUserId(String assetCat,
			String assetSubCat,
			String assetBrand, String userId);

	List<ServicePersonAssetsEntity> findByAssetCatAndAssetSubCatAndAssetModelAndUserId(String assetCat,
			String assetSubCat,
			String assetModel, String userId);

	List<ServicePersonAssetsEntity> findByAssetCatAndAssetSubCatAndUserId(String assetCat, String assetSubCat,
			String userId);

	List<ServicePersonAssetsEntity> findByAssetCatAndUserId(String assetCat, String userId);

	List<ServicePersonAssetsEntity> findByAssetSubCatAndUserId(String assetSubCat, String userId);

	List<ServicePersonAssetsEntity> findByLocationAndUserId(String location, String userId);

	List<ServicePersonAssetsEntity> findByAssetModelAndUserId(String assetModel, String userId);

	List<ServicePersonAssetsEntity> findByAssetBrandAndUserId(String assetBrand, String userId);

	List<ServicePersonAssetsEntity> findByUserIdAndAssetCatAndAssetSubCat(String userId, String assetCat,
			String assetSubCat);

	List<ServicePersonAssetsEntity> findByUserIdAndLocation(String userId, String location);

	List<ServicePersonAssetsEntity> findByUserIdAndAssetIdAndLocation(String userId, String assetId, String location);

}
