package com.application.mrmason.service;

import java.nio.file.AccessDeniedException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.application.mrmason.entity.AdminMaterialMaster;

public interface AdminMaterialMasterService{

	public List<AdminMaterialMaster> createAdminMaterialMaster(List<AdminMaterialMaster> requestDTO)throws AccessDeniedException;
	List<AdminMaterialMaster> updateAdminMaterialMasters(List<AdminMaterialMaster> updatedList)throws AccessDeniedException;
	public Page<AdminMaterialMaster> getAdminMaterialMaster(String materialCategory,
			String materialSubCategory, String brand, String modelNo, String size,String shape, Pageable pageable)
			throws AccessDeniedException;

}

