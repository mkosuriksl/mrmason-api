package com.application.mrmason.dto;

import java.util.List;

import com.application.mrmason.entity.QuotationEntity;

import lombok.Data;
@Data
public class ResponseGetQuotationDto {
	private String message;
	private boolean status;
	private List<QuotationEntity> workersData;
}