package com.application.mrmason.dto;

import java.util.List;

import com.application.mrmason.entity.AdminConstructionTasksManagement;

import lombok.Data;

@Data
public class ResponseGetAdminConstructionTasksManagementDto {
	private String message;
	private boolean status;
	private List<AdminConstructionTasksManagement> adminConstructionTasksManagement;
	private int currentPage;
	private int pageSize;
	private long totalElements;
	private int totalPages;
}