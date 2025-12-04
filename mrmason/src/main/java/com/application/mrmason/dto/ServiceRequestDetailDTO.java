package com.application.mrmason.dto;
import java.time.LocalDate;

import lombok.Data;

@Data
public class ServiceRequestDetailDTO {
    private String serviceLineId;
    private String requestId;
    private String servicePersonId;
    private String updatedBy;
    private LocalDate updatedDate;
}