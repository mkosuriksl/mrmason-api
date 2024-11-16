package com.application.mrmason.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SPMembRenewDetailsRequestDTO {

    private String membershipOrderIdLineItem;
    private int orderAmount;
    private LocalDateTime orderDate;
    private String orderPlacedBy;
    private String planId;
    private String status;
    private String storeId;

}
