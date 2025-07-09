package com.application.mrmason.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AdminStoreVerificationResponse<T> {
    private String message;
    private String status;
    private T data;
    
    private Integer currentPage;
    private Integer pageSize;
    private Long totalElements;
    private Integer totalPages;
    
    public AdminStoreVerificationResponse(String message, String status, T data) {
        this.message = message;
        this.status = status;
        this.data = data;
    }
}
