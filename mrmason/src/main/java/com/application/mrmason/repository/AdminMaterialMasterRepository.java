package com.application.mrmason.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.application.mrmason.entity.AdminMaterialMaster;

public interface AdminMaterialMasterRepository extends JpaRepository<AdminMaterialMaster, String> {

	Optional<AdminMaterialMaster> findBySkuId(String skuId);

	@Query("SELECT DISTINCT amm.brand FROM AdminMaterialMaster amm WHERE amm.materialCategory = :materialCategory And amm.materialSubCategory = :materialSubCategory")
	List<String> findDistinctBrandByMaterialCategory(String materialCategory, String materialSubCategory);

	List<AdminMaterialMaster> findByUpdatedBy(@Param("updatedBy") String updatedBy);

	@Query("SELECT a FROM AdminMaterialMaster a WHERE a.skuId = :skuId")
	List<AdminMaterialMaster> findBySkuIds(@Param("skuId") String skuId);

}
