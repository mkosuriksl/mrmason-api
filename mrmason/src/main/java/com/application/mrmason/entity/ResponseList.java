package com.application.mrmason.entity;

import java.util.List;

import lombok.Data;

@Data
public class ResponseList<T> {
    private String message;
    private boolean status;
    private List<T> data;
    private int currentPage;
    private int pageSize;
    private long totalElements;
    private int totalPages;
}

