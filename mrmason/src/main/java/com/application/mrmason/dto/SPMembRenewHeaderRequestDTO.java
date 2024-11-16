package com.application.mrmason.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SPMembRenewHeaderRequestDTO {
    private String membershipOrderId;
    private int orderAmount;
    private String orderPlacedBy;
    private String status;

    
}