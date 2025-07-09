package com.application.mrmason.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.application.mrmason.entity.Rental;

@Repository
public interface RentalRepo extends JpaRepository<Rental, String>{
	Rental findByAssetIdAndUserId(String assetId,String userId);
	List<Rental> findByAssetIdOrUserId(String assetId,String userId);

//	List<Rental> findByAssetIdIn(List<String> assetIds);
	Page<Rental> findByAssetIdIn(List<String> assetIds, Pageable pageable);
	Page<Rental> findByAssetId(String assetId, Pageable pageable);
    Page<Rental> findByUserId(String userId, Pageable pageable);
    Page<Rental> findByAssetIdAndUserId(String assetId, String userId, Pageable pageable);
}
