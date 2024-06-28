package com.application.mrmason.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.application.mrmason.entity.AdminAssetCategory;

@Repository
public interface AdminAssetCategoryRepo extends JpaRepository<AdminAssetCategory, Integer> {

	List<AdminAssetCategory> findByAssetCategoryOrderByCreateDateDesc(String assetCategory);

	List<AdminAssetCategory> findByAssetCategoryNotOrderByCreateDateDesc(String assetCategory);

}
