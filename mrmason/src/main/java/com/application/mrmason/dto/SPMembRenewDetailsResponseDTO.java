package com.application.mrmason.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SPMembRenewDetailsResponseDTO {

    private String membershipOrderIdLineItem;
    private String membershipOrderId;
    private String storeId;
    private String planId;
    private int orderAmount;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd, HH:mm:ss")
    private LocalDateTime orderDate;
    private String orderPlacedBy;
    private String status;

}
