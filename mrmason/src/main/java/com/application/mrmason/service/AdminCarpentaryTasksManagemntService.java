package com.application.mrmason.service;

import java.nio.file.AccessDeniedException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.application.mrmason.dto.AdminCarpentaryTasksManagemntRequestDTO;
import com.application.mrmason.entity.AdminCarpentaryTasksManagemnt;
import com.application.mrmason.enums.RegSource;

public interface AdminCarpentaryTasksManagemntService {
	public List<AdminCarpentaryTasksManagemnt> createAdmin(AdminCarpentaryTasksManagemntRequestDTO requestDTO);
	public List<AdminCarpentaryTasksManagemnt> updateAdmin(List<AdminCarpentaryTasksManagemnt> taskList);
	Page<AdminCarpentaryTasksManagemnt> getAdmin(String serviceCategory, String taskName,
			String taskId, String adminTaskId,RegSource regSource, Pageable pageable) throws AccessDeniedException;
}
