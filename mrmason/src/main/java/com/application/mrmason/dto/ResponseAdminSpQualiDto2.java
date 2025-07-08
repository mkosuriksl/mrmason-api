package com.application.mrmason.dto;

import java.util.List;

import com.application.mrmason.entity.AdminSpQualification;

import lombok.Data;

@Data
public class ResponseAdminSpQualiDto2 {

	private String message;
	private boolean status;
	private List<AdminSpQualification> getData;
	private int currentPage;
	private int pageSize;
	private long totalElements;
	private int totalPages;
}
