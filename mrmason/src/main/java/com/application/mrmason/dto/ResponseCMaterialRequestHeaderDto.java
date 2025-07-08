package com.application.mrmason.dto;

import java.util.List;

import com.application.mrmason.entity.CMaterialRequestHeaderEntity;

import lombok.Data;

@Data
public class ResponseCMaterialRequestHeaderDto {
    private String message;
    private boolean status;
    private List<CMaterialRequestHeaderEntity> data;
    private int currentPage;
    private int pageSize;
    private long totalElements;
    private int totalPages;
}

