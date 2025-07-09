package com.application.mrmason.dto;

import java.util.List;

import com.application.mrmason.entity.AdminServiceName;

import lombok.Data;

@Data
public class ResponseListAdminServiceDto {
	private String message;
	private boolean status;
	private List<AdminServiceName> data;
	private int currentPage;
    private int pageSize;
    private long totalElements;
    private int totalPages;
}
