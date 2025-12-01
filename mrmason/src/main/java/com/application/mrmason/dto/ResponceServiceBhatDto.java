package com.application.mrmason.dto;

import lombok.Data;

@Data
public class ResponceServiceBhatDto {
    private String message;
    private boolean status;
    private ServiceCategoryBhatDto data;
}
