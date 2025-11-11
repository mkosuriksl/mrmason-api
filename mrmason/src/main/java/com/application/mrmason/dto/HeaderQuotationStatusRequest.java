package com.application.mrmason.dto;

import com.application.mrmason.entity.SPWAStatus;

import lombok.Data;

@Data
public class HeaderQuotationStatusRequest {
    private String quotationId;
    private SPWAStatus status;
}


