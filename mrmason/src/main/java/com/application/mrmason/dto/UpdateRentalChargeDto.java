package com.application.mrmason.dto;

import lombok.Data;

@Data
public class UpdateRentalChargeDto {
    
    private String assetId;
    private String userId;
    private String amountper30days;
    private String amountPerDay;
    private String pickup;
    private String availableLocation;
    private String isAvailRent;
    private String delivery;
    
}
