package com.application.mrmason.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseSiteMeasurementDTO {
    private String message;
    private boolean status;
    private SiteMeasurementDTO data;
    private List<SiteMeasurementDTO> measurementList;
}