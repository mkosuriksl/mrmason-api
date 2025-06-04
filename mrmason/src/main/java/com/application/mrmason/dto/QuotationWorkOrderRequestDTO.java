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
public class QuotationWorkOrderRequestDTO {
	private String quotationWorkOrder;
	private String quotationId;
	private Date expectedStartDate;
	private Date expectedEndDate;
	private SPWAStatus status;
}
