package com.application.mrmason.dto;

import java.util.Date;

import lombok.Data;

@Data
public class UpdateSiteMeasurementStatusResponseDTO {
    private String serviceRequestId;
    private String updatedBy;
    private String status;
    private String comments;
    private Date updatedDate;
}

