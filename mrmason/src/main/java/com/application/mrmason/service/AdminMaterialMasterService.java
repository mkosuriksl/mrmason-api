package com.application.mrmason.service;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.application.mrmason.dto.MaterialGroupDTO;
import com.application.mrmason.dto.ResponseModel;
import com.application.mrmason.entity.AdminMaterialMaster;
import com.application.mrmason.enums.RegSource;

public interface AdminMaterialMasterService{

	public List<MaterialGroupDTO> createAdminMaterialMaster(
	        List<MaterialGroupDTO> requestGroups,
	        RegSource regSource) throws AccessDeniedException;
	List<AdminMaterialMaster> updateAdminMaterialMasters(List<AdminMaterialMaster> updatedList,RegSource regSource)throws AccessDeniedException;
	public Page<AdminMaterialMaster> getAdminMaterialMaster(String materialCategory,
			String materialSubCategory, String brand, String modelNo, String size,String shape, Pageable pageable)
			throws AccessDeniedException;
	public ResponseEntity<ResponseModel> uploadDoc(RegSource regSource,String skuId, MultipartFile materialMasterImage1, MultipartFile materialMasterImage2,
			MultipartFile materialMasterImage3,MultipartFile materialMasterImage4,
            MultipartFile materialMasterImage5)throws AccessDeniedException;

	public List<String> findDistinctBrandByMaterialCategory(String materialCategory, Map<String, String> requestParams);
	public List<String> findDistinctMaterialCategory() ;
}

