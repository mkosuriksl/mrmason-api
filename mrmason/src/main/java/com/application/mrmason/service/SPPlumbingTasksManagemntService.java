package com.application.mrmason.service;

import java.nio.file.AccessDeniedException;
import java.util.List;

import org.springframework.data.domain.Page;

import com.application.mrmason.dto.SPPlumbingTaskRequestDTO;
import com.application.mrmason.dto.TaskResponseDto;
import com.application.mrmason.entity.SPPlumbingTasksManagemnt;
import com.application.mrmason.enums.RegSource;

public interface SPPlumbingTasksManagemntService {
	public List<SPPlumbingTasksManagemnt> createAdmin(RegSource regSource,SPPlumbingTaskRequestDTO requestDTO)throws AccessDeniedException;
	public List<SPPlumbingTasksManagemnt> updateAdmin(RegSource regSource,List<SPPlumbingTasksManagemnt> taskList)throws AccessDeniedException ;
//	public List<TaskResponseDto> getTaskDetails(String serviceCategory, String taskId, String taskName,RegSource regSource) throws AccessDeniedException;
	public Page<TaskResponseDto> getTaskDetails(String serviceCategory, String taskId, String taskName,
            RegSource regSource, int page, int size) throws AccessDeniedException;
}
	
