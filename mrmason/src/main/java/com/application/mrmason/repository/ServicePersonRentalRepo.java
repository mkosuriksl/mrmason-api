package com.application.mrmason.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;
import com.application.mrmason.entity.ServicePersonRentalEntity;
import java.util.Optional;

@Repository
public interface ServicePersonRentalRepo extends JpaRepository<ServicePersonRentalEntity, String> {

	@Query("SELECT r FROM ServicePersonRentalEntity r WHERE "
        + "(:availableLocation IS NULL OR r.availableLocation = :availableLocation) AND "
        + "(:assetIds IS NULL OR r.assetId IN :assetIds) "
        + "ORDER BY r.id DESC")
List<ServicePersonRentalEntity> searchRentals(
        @Param("availableLocation") String availableLocation,
        @Param("assetIds") List<String> assetIds);


	@Query("SELECT r FROM ServicePersonRentalEntity r WHERE "
			+ "(:userId IS NULL OR r.userId = :userId) AND "
			+ "(:assetId IS NULL OR r.assetId = :assetId) AND "
			+ "(:availableLocation IS NULL OR r.availableLocation = :availableLocation) AND "
			+ "(:assetIds IS NULL OR r.assetId IN :assetIds) "
			+ "ORDER BY r.id DESC")
	List<ServicePersonRentalEntity> searchRentals(
			@Param("userId") String userId,
			@Param("assetId") String assetId,
			@Param("availableLocation") String availableLocation,
			@Param("assetIds") List<String> assetIds);

	Optional<ServicePersonRentalEntity> findByAssetIdAndUserId(String assetId, String userId);

}
