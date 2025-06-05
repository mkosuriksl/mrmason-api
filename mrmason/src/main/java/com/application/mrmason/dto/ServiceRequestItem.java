package com.application.mrmason.dto;

import java.util.List;

import lombok.Data;

@Data
public class ServiceRequestItem {
	private String taskDescription;
	private String taskId;
    private List<MeasurementDTO> measurements;
	public ServiceRequestItem(String taskDescription, String taskId, List<MeasurementDTO> measurements) {
		super();
		this.taskDescription = taskDescription;
		this.taskId = taskId;
		this.measurements = measurements;
	}
    
}
