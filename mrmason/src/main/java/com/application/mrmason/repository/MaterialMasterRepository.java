package com.application.mrmason.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.application.mrmason.entity.AdminMaterialMaster;
import com.application.mrmason.entity.MaterialMaster;

public interface MaterialMasterRepository extends JpaRepository<MaterialMaster, String> {

	boolean existsByMsCatmsSubCatmsBrandSkuId(String msCatmsSubCatmsBrandSkuId);

	Optional<MaterialMaster> findByMsCatmsSubCatmsBrandSkuId(String msCatmsSubCatmsBrandSkuId);
	
	@Query("SELECT m FROM MaterialMaster m "
			+ "WHERE (:materialCategory IS NULL OR m.materialCategory = :materialCategory) "
			+ "AND (:materialSubCategory IS NULL OR m.materialSubCategory = :materialSubCategory) "
			+ "AND (:brand IS NULL OR m.brand = :brand)")
	List<MaterialMaster> searchMaterials(@Param("materialCategory") String materialCategory,
			@Param("materialSubCategory") String materialSubCategory, @Param("brand") String brand);
}

