package com.application.mrmason.dto;

import java.util.List;

import com.application.mrmason.entity.SpWorkers;

import lombok.Data;
@Data
public class ResponsesGetWorkerDto {
	private String message;
	private boolean status;
	private List<SpWorkers> workersData;
	private Userdto userData;
	private int currentPage;
    private int pageSize;
    private long totalElements;
    private int totalPages;
}