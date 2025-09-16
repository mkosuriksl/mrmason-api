package com.application.mrmason.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.application.mrmason.entity.AdminMaterialMaster;

public interface AdminMaterialMasterRepository extends JpaRepository<AdminMaterialMaster, String>{

	Optional<AdminMaterialMaster> findBySkuId(String skuId);
	@Query("SELECT DISTINCT amm.brand FROM AdminMaterialMaster amm WHERE amm.materialCategory = :materialCategory")
	List<String> findDistinctBrandByMaterialCategory(String materialCategory);
	
	@Query("SELECT DISTINCT s.materialCategory FROM AdminMaterialMaster s WHERE s.materialCategory IS NOT NULL")
    List<String> findDistinctMaterialCategory();
	
}


