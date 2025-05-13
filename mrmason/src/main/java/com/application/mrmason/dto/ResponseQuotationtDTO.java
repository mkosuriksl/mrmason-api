package com.application.mrmason.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseQuotationtDTO {
    private String message;
    private boolean status;
    private QuotationDTO data;
}