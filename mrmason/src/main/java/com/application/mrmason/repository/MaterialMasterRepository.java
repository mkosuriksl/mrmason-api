package com.application.mrmason.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.application.mrmason.entity.MaterialMaster;

public interface MaterialMasterRepository extends JpaRepository<MaterialMaster, String> {

	boolean existsByMsCatmsSubCatmsBrandSkuId(String msCatmsSubCatmsBrandSkuId);
}

