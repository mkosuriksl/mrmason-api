package com.application.mrmason.dto;

import java.util.List;

import lombok.Data;

@Data
public class UpdateCustomerOrderRequestDto {

	private String orderId;
    private List<CustomerOrderDetailsDto> orderDetailsList;
}
