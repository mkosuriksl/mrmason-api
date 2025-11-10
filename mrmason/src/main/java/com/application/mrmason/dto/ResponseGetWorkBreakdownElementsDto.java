package com.application.mrmason.dto;

import java.util.List;

import com.application.mrmason.entity.WorkBreakdownElements;

import lombok.Data;

@Data
public class ResponseGetWorkBreakdownElementsDto {
	private String message;
    private boolean status;
    private List<WorkBreakdownElements> workBreakdownElements;
    private int currentPage;
    private int pageSize;
    private long totalElements;
    private int totalPages;
}
