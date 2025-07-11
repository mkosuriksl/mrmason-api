package com.application.mrmason.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;

import com.application.mrmason.dto.AdminPaintOnlyTaskRequestDTO;
import com.application.mrmason.entity.AdminPaintOnlyTasksManagemnt;
import com.application.mrmason.enums.RegSource;

public interface AdminPaintOnlyTasksManagemntService {

	public List<AdminPaintOnlyTasksManagemnt> createAdmin(AdminPaintOnlyTaskRequestDTO requestDTO);
	public List<AdminPaintOnlyTasksManagemnt> updateAdmin(List<AdminPaintOnlyTasksManagemnt> taskList);
	Page<AdminPaintOnlyTasksManagemnt> getServiceRequestPaintQuotationService(String serviceCategory, String taskName,
			String taskId, String adminTaskId,RegSource regSource, Pageable pageable) throws AccessDeniedException;
}
