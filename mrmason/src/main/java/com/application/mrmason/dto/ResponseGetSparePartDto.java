package com.application.mrmason.dto;

import java.util.List;

import com.application.mrmason.entity.AdminPaintOnlyTasksManagemnt;

import lombok.Data;

@Data
public class ResponseGetSparePartDto {
	private String message;
	private boolean status;
	private List<SparePartEntity> sparePartdto;
	private int currentPage;
	private int pageSize;
	private long totalElements;
	private int totalPages;
}
