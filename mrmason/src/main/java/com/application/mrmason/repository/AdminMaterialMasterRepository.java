package com.application.mrmason.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.application.mrmason.entity.AdminMaterialMaster;

public interface AdminMaterialMasterRepository extends JpaRepository<AdminMaterialMaster, String>{

	Optional<AdminMaterialMaster> findBySkuId(String skuId);
	

	
}


