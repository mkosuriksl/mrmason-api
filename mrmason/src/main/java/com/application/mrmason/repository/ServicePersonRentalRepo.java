package com.application.mrmason.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.application.mrmason.entity.ServicePersonAssetsEntity;
import com.application.mrmason.entity.ServicePersonRentalEntity;

@Repository
public interface ServicePersonRentalRepo extends JpaRepository<ServicePersonRentalEntity, String> {

	ServicePersonRentalEntity findByAssetIdAndUserId(String assetId, String userId);

	List<ServicePersonRentalEntity> findByAssetIdOrUserId(String assetId, String userId);

	List<ServicePersonRentalEntity> findByAssetIdIn(List<String> assetIds);

	List<ServicePersonAssetsEntity> findByUserIdAndAssetId(String userId, String assetId);

	List<ServicePersonRentalEntity> findByUserIdAndAssetIdAndAvailableLocation(String userId, String assetId,
			String availableLocation);

	List<ServicePersonRentalEntity> findByAssetIdInAndAvailableLocation(List<String> assetIds,
			String availableLocation);

	List<ServicePersonRentalEntity> findByUserIdAndAvailableLocation(String userId, String availableLocation);

	List<ServicePersonRentalEntity> findByAvailableLocation(String availableLocation);

}
