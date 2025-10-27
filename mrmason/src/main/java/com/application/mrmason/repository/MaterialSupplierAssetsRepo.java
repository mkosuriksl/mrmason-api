package com.application.mrmason.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.application.mrmason.entity.MaterialSupplierAssets;

@Repository
public interface MaterialSupplierAssetsRepo extends JpaRepository<MaterialSupplierAssets, Long> {

	@Query("SELECT u FROM MaterialSupplierAssets u WHERE u.assetId = :assetId")
	Optional<MaterialSupplierAssets> findByAssetIdUploadImage(@Param("assetId") String assetId);

}
