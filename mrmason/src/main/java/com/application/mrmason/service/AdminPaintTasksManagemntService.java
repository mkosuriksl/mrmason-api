package com.application.mrmason.service;

import java.nio.file.AccessDeniedException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.application.mrmason.dto.AdminPaintTaskRequestDTO;
import com.application.mrmason.entity.AdminPaintTasksManagemnt;
import com.application.mrmason.enums.RegSource;

public interface AdminPaintTasksManagemntService {
	public List<AdminPaintTasksManagemnt> createAdmin(AdminPaintTaskRequestDTO requestDTO);
	public List<AdminPaintTasksManagemnt> updateAdmin(List<AdminPaintTasksManagemnt> taskList);
	Page<AdminPaintTasksManagemnt> getServiceRequestPaintQuotationService(String serviceCategory, String taskName,
			String taskId, String adminTaskId,RegSource regSource, Pageable pageable) throws AccessDeniedException;
}
