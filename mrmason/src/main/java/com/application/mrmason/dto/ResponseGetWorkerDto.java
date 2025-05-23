package com.application.mrmason.dto;

import java.util.List;

import lombok.Data;

@Data
public class ResponseGetWorkerDto {
	private String message;
	private boolean status;
//	private List<SpWorkers> workersData;
	private List<SPWorkerAssignmentDTO> spworkerAssignment;
	private int currentPage;
	private int pageSize;
	private long totalElements;
	private int totalPages;
}