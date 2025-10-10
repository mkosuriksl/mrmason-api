package com.application.mrmason.dto;

import java.util.List;

import com.application.mrmason.entity.AdminMaterialMaster;

import lombok.Data;

@Data
public class ResponseGetAdminMaterialMasterDto {
	private String message;
	private boolean status;
	private List<AdminMaterialMasterResponseWithImageDto> getAdminMaterialMaster;
	private int currentPage;
	private int pageSize;
	private long totalElements;
	private int totalPages;
}