package com.application.mrmason.dto;

import java.util.List;

import com.application.mrmason.entity.APIKEY;

import lombok.Data;

@Data
public class ResponseGetAPIkeyDto {
	private String message;
	private boolean status;
	private List<APIKEY> apikeys;
	private int currentPage;
	private int pageSize;
	private long totalElements;
	private int totalPages;
}
