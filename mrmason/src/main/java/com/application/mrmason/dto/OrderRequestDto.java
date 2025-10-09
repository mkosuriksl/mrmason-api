package com.application.mrmason.dto;

import java.util.List;

import lombok.Data;

@Data
public class OrderRequestDto {
    private String customerCartOrderId;
    private String customerId;
    private String location;
    private String deliveryMethod;
    private String userIdstoreId;
    private List<OrderDetailsDto> orderDetailsList;

}


