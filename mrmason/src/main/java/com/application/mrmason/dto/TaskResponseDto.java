package com.application.mrmason.dto;

import java.util.List;

import lombok.Data;

@Data
public class TaskResponseDto {
	private String serviceCategory;
    private String taskId;
    private String taskName;
    private List<MeasureTaskDto> measureTasks;
}
