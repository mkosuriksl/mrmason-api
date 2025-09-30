package com.application.mrmason.dto;

import java.util.List;

import lombok.Data;

@Data
public class CustomerGetOrderResponseDTO {
	private String orderId;
	private String userId;
	private List<CustomerOrderDetailsDto> orderDetailsList;
	private int currentPage;
	private int pageSize;
	private long totalElements;
	private int totalPages;
}
