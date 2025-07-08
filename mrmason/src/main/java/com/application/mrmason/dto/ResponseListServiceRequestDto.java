package com.application.mrmason.dto;

import java.util.List;

import lombok.Data;

@Data
public class ResponseListServiceRequestDto {
	private String message;
	private boolean status;
//	private List<ServiceRequest> data;
	List<ServiceRequestWithCustomerDTO> data;
	private int currentPage;
    private int pageSize;
    private long totalElement;
    private int totalPages;

}
