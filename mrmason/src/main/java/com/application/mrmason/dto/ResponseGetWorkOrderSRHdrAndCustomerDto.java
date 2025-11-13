package com.application.mrmason.dto;

import java.util.List;

import lombok.Data;

@Data
public class ResponseGetWorkOrderSRHdrAndCustomerDto {
	private String message;
    private boolean status;
    private List<WorkOrderCustomerResponseDto> workSRHeaderQuotation;
    private List<CustomerBasicDto>customerBasicDtos;
    private int currentPage;
    private int pageSize;
    private long totalElements;
    private int totalPages;
}
