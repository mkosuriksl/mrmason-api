package com.application.mrmason.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.application.mrmason.entity.MaterialSupplier;


public interface MaterialSupplierRepository extends JpaRepository<MaterialSupplier, String> {

	boolean existsBySupplierIdAndMaterialLineItem(String userId, String materialLineItem);

}
