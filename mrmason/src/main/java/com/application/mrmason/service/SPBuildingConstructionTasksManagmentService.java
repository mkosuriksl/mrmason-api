package com.application.mrmason.service;

import com.application.mrmason.dto.SPBuildingConstructionTaskRequestDTO;
import com.application.mrmason.dto.TaskResponseDto;
import com.application.mrmason.entity.SPBuildingConstructionTasksManagment;
import com.application.mrmason.enums.RegSource;
import java.nio.file.AccessDeniedException;
import java.util.List;

public interface SPBuildingConstructionTasksManagmentService {
    List<SPBuildingConstructionTasksManagment> createAdmin(RegSource regSource, SPBuildingConstructionTaskRequestDTO requestDTO) throws AccessDeniedException;
    List<SPBuildingConstructionTasksManagment> updateAdmin(RegSource regSource, List<SPBuildingConstructionTasksManagment> taskList) throws AccessDeniedException;
	public List<TaskResponseDto> getTaskDetails(String serviceCategory, String taskId, String taskName,RegSource regSource)throws AccessDeniedException ;
}
