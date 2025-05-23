package com.application.mrmason.service;

import java.nio.file.AccessDeniedException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.application.mrmason.dto.AdminPlumbingTasksManagemntRequestDTO;
import com.application.mrmason.entity.AdminPlumbingTasksManagemnt;
import com.application.mrmason.enums.RegSource;

public interface AdminPlumbingTasksManagemntService {
	public List<AdminPlumbingTasksManagemnt> createAdmin(AdminPlumbingTasksManagemntRequestDTO requestDTO);
	public List<AdminPlumbingTasksManagemnt> updateAdmin(List<AdminPlumbingTasksManagemnt> taskList);
	Page<AdminPlumbingTasksManagemnt> getAdmin(String serviceCategory, String taskName,
			String taskId, String adminTaskId,RegSource regSource, Pageable pageable) throws AccessDeniedException;
}
