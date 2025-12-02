package com.application.mrmason.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SparePartDto {
    private String requestIdSkuId;
    private String requestId;
    private String sparePart;
    private String skuId;
    private String brand;
    private String model;
    private Double amount;
    private Double discount;
    private Double gst;
    private Double totalAmount;
    private String warranty;
    private String updatedBy;
    private Date updatedDate;
    private String userId;
}

