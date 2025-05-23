package com.application.mrmason.dto;

import java.util.List;

import com.application.mrmason.entity.AdminPlumbingTasksManagemnt;

import lombok.Data;

@Data
public class ResponseGetAdminPlumbingTasksManagemntManagementDto {
	private String message;
	private boolean status;
	private List<AdminPlumbingTasksManagemnt> adminPlumbingTasksManagemnt;
	private int currentPage;
	private int pageSize;
	private long totalElements;
	private int totalPages;
}