package com.application.mrmason.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseSiteMeasurementDTO {
    private String message;
    private boolean status;
    private SiteMeasurementDTO data;
}