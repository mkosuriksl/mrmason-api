package com.application.mrmason.dto;

import java.util.List;

import lombok.Data;

@Data
public class CustomerOrderRequestDto {
	private String cId;
	private List<CustomerOrderDetailsDto> orderDetailsList;
}
