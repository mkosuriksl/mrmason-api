package com.application.mrmason.dto;

import java.util.List;

import lombok.Data;

@Data
public class ResponseCustomerAssetsDto {
    private String message;
    private boolean status;
    private List<?> assets;
    private int currentPage;
    private int pageSize;
    private long totalElements;
    private int totalPages;
}
