package com.application.mrmason.dto;

import java.util.List;

import com.application.mrmason.entity.AdminPaintTasksManagemnt;

import lombok.Data;

@Data
public class ResponseGetAdminPaintTasksManagemntDto {
	private String message;
	private boolean status;
	private List<AdminPaintTasksManagemnt> serviceRequestPaintQuotation;
	private int currentPage;
	private int pageSize;
	private long totalElements;
	private int totalPages;
}