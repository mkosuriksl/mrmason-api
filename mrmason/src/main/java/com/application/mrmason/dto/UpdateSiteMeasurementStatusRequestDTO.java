package com.application.mrmason.dto;

import java.util.Date;

import lombok.Data;

@Data
public class UpdateSiteMeasurementStatusRequestDTO {
	private String serviceRequestId;
    private Date updatedDate;
    private String updatedBy;
    private String status;
    private String comments;
}

