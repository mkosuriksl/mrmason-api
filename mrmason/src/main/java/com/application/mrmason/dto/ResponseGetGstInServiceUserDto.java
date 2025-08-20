package com.application.mrmason.dto;

import java.util.List;

import com.application.mrmason.entity.GstInServiceUser;

import lombok.Data;

@Data
public class ResponseGetGstInServiceUserDto {
	private String message;
	private boolean status;
	private List<GstInServiceUser> gstInServiceUsers;
	private int currentPage;
	private int pageSize;
	private long totalElements;
	private int totalPages;
}