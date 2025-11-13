package com.application.mrmason.dto;

import com.application.mrmason.entity.SPWAStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkOrderCustomerResponseDto {
	private String workOrderId;
    private String quotationId;
    private String quotedDate;
    private SPWAStatus status;
    private String spId;
    private String updatedBy;
    private String updatedDate;
}

