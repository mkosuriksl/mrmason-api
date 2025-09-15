package com.application.mrmason.dto;

import java.util.List;

import com.application.mrmason.entity.MaterialMaster;

import lombok.Data;

@Data
public class ResponseGetMaterialMasterDto {
	private String message;
	private boolean status;
	private List<MaterialMaster> materialMasters;
	private int currentPage;
	private int pageSize;
	private long totalElements;
	private int totalPages;
}