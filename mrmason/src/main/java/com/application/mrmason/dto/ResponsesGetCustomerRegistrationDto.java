package com.application.mrmason.dto;

import java.util.List;

import lombok.Data;
@Data
public class ResponsesGetCustomerRegistrationDto {
	private String message;
	private boolean status;
	private List<CustomerRegistrationRespForSPAdmin> customerData;
	private int currentPage;
    private int pageSize;
    private long totalElements;
    private int totalPages;
}