package com.application.mrmason.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.application.mrmason.entity.WorkBreakdownElements;
import com.application.mrmason.enums.RegSource;

public interface WorkBreakdownElementsService {

	public WorkBreakdownElements addWorkBreakdownElements(WorkBreakdownElements adminMachineAssetsImage,RegSource regSource);

	public WorkBreakdownElements updateWorkBreakdownElements(WorkBreakdownElements adminMachineAssetsImage,RegSource regSource);

	public Page<WorkBreakdownElements> get(String woOrderNo, String taskId,String subTaskId, String actualStartDate, String actualEndDate, String tentativeStartdate, String tentaiveEnddate,
			Pageable pageable);
}
