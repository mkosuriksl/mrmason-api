package com.application.mrmason.dto;

import lombok.Data;

@Data
public class MaterialPricingRequestDto {
    private String userIdSku;  // required for update
    private Double mrp;
    private Double discount;
    private Double amount;
    private Double gst;
}
