package com.application.mrmason.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.application.mrmason.entity.ServicePersonAssetsEntity;

public interface ServicePersonAssetsRepo extends JpaRepository<ServicePersonAssetsEntity, Long> {

	@Query("SELECT a FROM ServicePersonAssetsEntity a WHERE a.userId = :userId AND "
			+ "(:assetId IS NULL OR a.assetId = :assetId) AND " + "(:assetCat IS NULL OR a.assetCat = :assetCat) AND "
			+ "(:assetSubCat IS NULL OR a.assetSubCat = :assetSubCat) AND "
			+ "(:assetBrand IS NULL OR a.assetBrand = :assetBrand) AND "
			+ "(:assetModel IS NULL OR a.assetModel = :assetModel) " + "ORDER BY a.id DESC")
	List<ServicePersonAssetsEntity> searchAssets(@Param("userId") String userId, @Param("assetId") String assetId,
			@Param("assetCat") String assetCat, @Param("assetSubCat") String assetSubCat,
			@Param("assetBrand") String assetBrand, @Param("assetModel") String assetModel);

	Optional<ServicePersonAssetsEntity> findByUserIdAndAssetId(String userId, String assetId);

	Optional<ServicePersonAssetsEntity> findAllByAssetId(String assetId);

}
