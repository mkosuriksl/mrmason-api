package com.application.mrmason.dto;

import java.util.List;

import com.application.mrmason.entity.AdminBhatServiceName;

import lombok.Data;

@Data
public class ResponseListAdminBhatServiceDto {
	private String message;
	private boolean status;
	private List<AdminBhatServiceName> data;
	private int currentPage;
    private int pageSize;
    private long totalElements;
    private int totalPages;
}
