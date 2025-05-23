package com.application.mrmason.dto;

import java.util.List;

import com.application.mrmason.entity.AdminPopTasksManagemnt;

import lombok.Data;

@Data
public class ResponseGetAdminPopTasksManagemntDto {
	private String message;
	private boolean status;
	private List<AdminPopTasksManagemnt> adminPopTasksManagemnt;
	private int currentPage;
	private int pageSize;
	private long totalElements;
	private int totalPages;
}