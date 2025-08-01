package com.application.mrmason.dto;

import java.util.List;

import com.application.mrmason.entity.AdminAmcRate;

import lombok.Data;
@Data
public class ResponseListAdminAmcRate {
	private String message;
	private boolean status;
	private List<AdminAmcRate> data;
	private int currentPage;
	private int pageSize;
	private long totalElements;
	private int totalPages;
	
}
