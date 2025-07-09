package com.application.mrmason.dto;

import java.util.List;

import lombok.Data;

@Data
public class ResponseAddServiceGetDto {
	private String message;
	private boolean status;
	private List<AdminServiceNameDto> getServiceId;
	private List<AddServicesDto> GetAddServicesData;
	
	private int currentPage;
    private int pageSize;
    private long totalElements;
    private int totalPages;
	
}
