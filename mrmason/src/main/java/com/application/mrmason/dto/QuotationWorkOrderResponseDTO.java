package com.application.mrmason.dto;

import java.util.Date;

import com.application.mrmason.entity.SPWAStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuotationWorkOrderResponseDTO {
	private String quotationWorkOrder;
	private String quotationId;

	private Date woGenerateDate;
	private String updatedBy;
	private Date updatedDate;
	private Date expectedStartDate;

	private Date expectedEndDate;
	private String spId;

	private SPWAStatus status;
}
