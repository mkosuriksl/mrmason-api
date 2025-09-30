package com.application.mrmason.dto;

import java.util.List;

import lombok.Data;

@Data
public class CustomerOrderRequestDto {
	private String userId;
	private List<CustomerOrderDetailsDto> orderDetailsList;
}
