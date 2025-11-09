package com.application.mrmason.dto;

import java.util.List;

import com.application.mrmason.entity.ServiceRequestHeaderAllQuotation2;

import lombok.Data;

@Data
public class ResponseGetWorkOrderSRHeaderQuotationDto {
	private String message;
    private boolean status;
    private List<ServiceRequestHeaderAllQuotation2> workSRHeaderQuotation;
    private int currentPage;
    private int pageSize;
    private long totalElements;
    private int totalPages;
}
