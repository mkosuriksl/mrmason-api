package com.application.mrmason.service;

import java.nio.file.AccessDeniedException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.application.mrmason.dto.AdminConstructionTasksManagementRequestDTO;
import com.application.mrmason.entity.AdminConstructionTasksManagement;
import com.application.mrmason.enums.RegSource;

public interface AdminConstructionTasksManagementService {
	public List<AdminConstructionTasksManagement> createAdmin(AdminConstructionTasksManagementRequestDTO requestDTO);
	public List<AdminConstructionTasksManagement> updateAdmin(List<AdminConstructionTasksManagement> taskList);
	Page<AdminConstructionTasksManagement> getAdmin(String serviceCategory, String taskName,
			String taskId, String adminTaskId,RegSource regSource, Pageable pageable) throws AccessDeniedException;
}
