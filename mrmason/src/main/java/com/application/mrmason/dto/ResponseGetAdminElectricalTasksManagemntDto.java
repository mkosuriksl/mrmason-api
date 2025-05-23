package com.application.mrmason.dto;

import java.util.List;

import com.application.mrmason.entity.AdminElectricalTasksManagement;

import lombok.Data;

@Data
public class ResponseGetAdminElectricalTasksManagemntDto {
	private String message;
	private boolean status;
	private List<AdminElectricalTasksManagement> serviceRequestElectricalQuotation;
	private int currentPage;
	private int pageSize;
	private long totalElements;
	private int totalPages;
}