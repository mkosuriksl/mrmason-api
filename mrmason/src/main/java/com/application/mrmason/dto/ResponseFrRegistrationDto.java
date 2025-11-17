package com.application.mrmason.dto;

import java.util.List;

import lombok.Data;

@Data
public class ResponseFrRegistrationDto {
	private String message;
	private boolean status;

	private List<FrRegistrationResponseDto> frReg;

	private int currentPage;
	private int pageSize;
	private long totalElements;
	private int totalPages;
}
