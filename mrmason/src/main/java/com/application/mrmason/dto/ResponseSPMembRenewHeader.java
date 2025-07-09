package com.application.mrmason.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseSPMembRenewHeader<T> {
    private String message;
    private String status;
    private T data;

    private int currentPage;
    private int totalPages;
    private long totalElements;
    
    public ResponseSPMembRenewHeader(String message, String status, T data) {
        this.message = message;
        this.status = status;
        this.data = data;
    }

}
