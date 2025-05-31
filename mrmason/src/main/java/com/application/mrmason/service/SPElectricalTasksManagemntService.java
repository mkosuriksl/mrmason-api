package com.application.mrmason.service;

import java.nio.file.AccessDeniedException;
import java.util.List;

import com.application.mrmason.dto.SPElectricalTaskRequestDTO;
import com.application.mrmason.dto.TaskResponseDto;
import com.application.mrmason.entity.SPElectricalTasksManagemnt;
import com.application.mrmason.enums.RegSource;

public interface SPElectricalTasksManagemntService {
    public List<SPElectricalTasksManagemnt> createAdmin(RegSource regSource, SPElectricalTaskRequestDTO requestDTO) throws AccessDeniedException;
    public List<SPElectricalTasksManagemnt> updateAdmin(RegSource regSource, List<SPElectricalTasksManagemnt> taskList) throws AccessDeniedException;
	public List<TaskResponseDto> getTaskDetails(String serviceCategory, String taskId, String taskName) ;
}
