package com.application.mrmason.dto;

import java.util.List;

import com.application.mrmason.entity.FrServiceRole;

import lombok.Data;

@Data
public class ResponseServiceRoleDto {
	private String message;
	private boolean status;

	private List<FrServiceRole> frServiceRoles;

	private int currentPage;
	private int pageSize;
	private long totalElements;
	private int totalPages;
}
