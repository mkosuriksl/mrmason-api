package com.application.mrmason.service;

import java.nio.file.AccessDeniedException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.application.mrmason.dto.AdminPopTasksManagemntRequestDTO;
import com.application.mrmason.entity.AdminPopTasksManagemnt;
import com.application.mrmason.enums.RegSource;

public interface AdminPopTasksManagemntService {
	public List<AdminPopTasksManagemnt> createAdmin(AdminPopTasksManagemntRequestDTO requestDTO);

	public List<AdminPopTasksManagemnt> updateAdmin(List<AdminPopTasksManagemnt> taskList);

	Page<AdminPopTasksManagemnt> getAdmin(String serviceCategory, String taskName, String taskId, String adminTaskId,
			RegSource regSource, Pageable pageable) throws AccessDeniedException;
}
