package com.application.mrmason.service;

import java.nio.file.AccessDeniedException;
import java.util.List;
import com.application.mrmason.dto.SPPopTaskRequestDTO;
import com.application.mrmason.dto.TaskResponseDto;
import com.application.mrmason.entity.SPPopTasksManagemnt;
import com.application.mrmason.enums.RegSource;

public interface SPPopTasksManagemntService {
    List<SPPopTasksManagemnt> createAdmin(RegSource regSource, SPPopTaskRequestDTO requestDTO) throws AccessDeniedException;
    List<SPPopTasksManagemnt> updateAdmin(RegSource regSource, List<SPPopTasksManagemnt> taskList) throws AccessDeniedException;
    public List<TaskResponseDto> getTaskDetails(String serviceCategory, String taskId, String taskName);
}
