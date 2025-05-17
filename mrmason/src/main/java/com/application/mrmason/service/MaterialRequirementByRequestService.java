package com.application.mrmason.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.application.mrmason.entity.MaterialRequirementByRequest;
import com.application.mrmason.enums.RegSource;

public interface MaterialRequirementByRequestService {
	List<MaterialRequirementByRequest> createMaterialRequirementByRequest(List<MaterialRequirementByRequest> materialRequirementByRequest,RegSource regSource);
    List<MaterialRequirementByRequest> updateMaterialRequirementByRequest(List<MaterialRequirementByRequest> materialRequirementByRequest,RegSource regSource);
    public Page<MaterialRequirementByRequest> getMaterialRequirementByRequest(
			String materialCategory, String brand, String itemName,String reqIdLineId,String modelName, String modelCode, String reqId,
			String spId, String updatedBy, String status, Pageable pageable,RegSource regSource);

}
