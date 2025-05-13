package com.application.mrmason.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuotationDTO {
	private String reqId;

	private String customerId;

	private Integer quotedAmount;

	private String servicePersonId;

	private String updatedBy;

	private Date updatedDate;

	private String status;
	
	private String unit;

}