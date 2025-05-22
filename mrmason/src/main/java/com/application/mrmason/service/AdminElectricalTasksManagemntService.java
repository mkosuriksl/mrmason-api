package com.application.mrmason.service;

import java.nio.file.AccessDeniedException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.application.mrmason.dto.AdminElectricalTaskRequestDTO;
import com.application.mrmason.entity.AdminElectricalTasksManagement;
import com.application.mrmason.enums.RegSource;

public interface AdminElectricalTasksManagemntService {
	public List<AdminElectricalTasksManagement> createAdmin(AdminElectricalTaskRequestDTO requestDTO);
	public List<AdminElectricalTasksManagement> updateAdmin(List<AdminElectricalTasksManagement> taskList);
	Page<AdminElectricalTasksManagement> getAdmin(String serviceCategory, String taskName,
			String taskId, String adminTaskId,RegSource regSource, Pageable pageable) throws AccessDeniedException;
}
