package com.application.mrmason.dto;

import java.util.List;

import com.application.mrmason.entity.AdminSms;

import lombok.Data;

@Data
public class ResponseAdminSmsDto {
    private String message;
    private boolean status;
    private List<AdminSms> data;
    private int currentPage;
    private int pageSize;
    private long totalElements;
    private int totalPages;
}
