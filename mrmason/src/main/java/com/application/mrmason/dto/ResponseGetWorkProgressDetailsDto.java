package com.application.mrmason.dto;

import java.util.List;

import com.application.mrmason.entity.WorkProgressDetails;

import lombok.Data;

@Data
public class ResponseGetWorkProgressDetailsDto {
    private String message;
    private boolean status;
    private List<WorkProgressDetails> workProgressDetails;
    private int currentPage;
    private int pageSize;
    private long totalElements;
    private int totalPages;
}
