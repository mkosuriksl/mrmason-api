package com.application.mrmason.dto;

import java.util.List;

import com.application.mrmason.entity.AdminCarpentaryTasksManagemnt;

import lombok.Data;

@Data
public class ResponseGetAdminCarpentaryTasksManagemntManagementDto {
	private String message;
	private boolean status;
	private List<AdminCarpentaryTasksManagemnt> serviceRequestCarpentaryQuotation;
	private int currentPage;
	private int pageSize;
	private long totalElements;
	private int totalPages;
}