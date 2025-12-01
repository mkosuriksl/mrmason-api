package com.application.mrmason.dto;

import java.util.List;

import com.application.mrmason.entity.ServiceCategoryBhat;

import lombok.Data;

@Data
public class ResponseListServiceCatBhatDto {
    private String message;
    private boolean status;
    private List<ServiceCategoryBhat> data;
    private int currentPage;
    private int pageSize;
    private long totalElements;
    private int totalPages;
}
