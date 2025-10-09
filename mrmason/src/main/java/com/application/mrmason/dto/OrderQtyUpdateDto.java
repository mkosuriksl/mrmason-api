package com.application.mrmason.dto;

import com.application.mrmason.enums.OrderStatus;

import lombok.Data;

@Data
public class OrderQtyUpdateDto {
    private String lineItemId;
    private int orderQty;
    private OrderStatus orderStatus;
}
