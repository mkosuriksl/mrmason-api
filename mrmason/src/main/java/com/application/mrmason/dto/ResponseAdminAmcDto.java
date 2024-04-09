package com.application.mrmason.dto;

import com.application.mrmason.entity.AdminAmcRate;

import lombok.Data;

@Data
public class ResponseAdminAmcDto {
	private String message;
	private AdminAmcRate adminAmcRates;
}