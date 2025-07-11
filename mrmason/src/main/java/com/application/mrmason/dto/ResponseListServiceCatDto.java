package com.application.mrmason.dto;

import java.util.List;

import com.application.mrmason.entity.ServiceCategory;

import lombok.Data;
@Data
public class ResponseListServiceCatDto {
	private String message;
	private boolean status;
	private List<ServiceCategory> data;
	private int currentPage;
    private int pageSize;
    private long totalElements;
    private int totalPages;
}
