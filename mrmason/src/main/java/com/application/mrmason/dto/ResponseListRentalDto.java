package com.application.mrmason.dto;

import java.util.List;

import com.application.mrmason.entity.Rental;

import lombok.Data;

@Data
public class ResponseListRentalDto {
	private String message;
	private boolean status;
	private List<Rental> data;
	private int currentPage;
	private int pageSize;
	private long totalElements;
	private int totalPages;
}
