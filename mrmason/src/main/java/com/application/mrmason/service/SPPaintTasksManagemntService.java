package com.application.mrmason.service;

import java.nio.file.AccessDeniedException;
import java.util.List;

import com.application.mrmason.dto.SPPaintTaskRequestDTO;
import com.application.mrmason.dto.TaskResponseDto;
import com.application.mrmason.entity.SPPaintTasksManagemnt;
import com.application.mrmason.enums.RegSource;

public interface SPPaintTasksManagemntService {
	public List<SPPaintTasksManagemnt> createAdmin(RegSource regSource,SPPaintTaskRequestDTO requestDTO)throws AccessDeniedException;
	public List<SPPaintTasksManagemnt> updateAdmin(RegSource regSource,List<SPPaintTasksManagemnt> taskList)throws AccessDeniedException ;
	public List<TaskResponseDto> getTaskDetails(String serviceCategory, String taskId, String taskName,RegSource regSource)  throws AccessDeniedException;
}
	
