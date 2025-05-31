package com.application.mrmason.service;

import java.nio.file.AccessDeniedException;
import java.util.List;

import com.application.mrmason.dto.SPCarpentaryConstructionTaskRequestDto;
import com.application.mrmason.dto.SPPaintTaskRequestDTO;
import com.application.mrmason.entity.SPCarpentaryConstructionTasksManagement;
import com.application.mrmason.enums.RegSource;

public interface SPCarpentaryConstructionTasksManagementService {
	List<SPCarpentaryConstructionTasksManagement> createAdmin(RegSource regSource, SPCarpentaryConstructionTaskRequestDto requestDTO) throws AccessDeniedException;
	List<SPCarpentaryConstructionTasksManagement> updateAdmin(RegSource regSource, List<SPCarpentaryConstructionTasksManagement> taskList) throws AccessDeniedException;
}
