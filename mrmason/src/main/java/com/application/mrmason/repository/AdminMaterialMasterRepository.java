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
	List<String> findDistinctBrandByMaterialCategory(String materialCategory,String materialSubCategory);

//	@Query("SELECT DISTINCT s.materialCategory FROM AdminMaterialMaster s WHERE s.materialCategory IS NOT NULL")
//	List<String> findDistinctMaterialCategory();
	
	 @Query("SELECT s.materialCategory, s.materialSubCategory " +
	           "FROM AdminMaterialMaster s " +
	           "WHERE s.materialCategory IS NOT NULL AND s.materialSubCategory IS NOT NULL")
	    List<Object[]> findCategoryAndSubCategory();

//	@Query("SELECT m FROM AdminMaterialMaster m "
//			+ "WHERE (:materialCategory IS NULL OR m.materialCategory = :materialCategory) "
//			+ "AND (:materialSubCategory IS NULL OR m.materialSubCategory = :materialSubCategory) "
//			+ "AND (:brand IS NULL OR m.brand = :brand)")
//	List<AdminMaterialMaster> searchMaterials(@Param("materialCategory") String materialCategory,
//			@Param("materialSubCategory") String materialSubCategory, @Param("brand") String brand);

	List<AdminMaterialMaster> findByUpdatedBy(@Param("updatedBy") String updatedBy);

	@Query("SELECT a FROM AdminMaterialMaster a WHERE a.skuId = :skuId")
    List<AdminMaterialMaster> findBySkuIds(@Param("skuId") String skuId);

}
