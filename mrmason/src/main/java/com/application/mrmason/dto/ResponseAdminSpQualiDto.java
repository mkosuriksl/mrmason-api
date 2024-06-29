package com.application.mrmason.dto;

import com.application.mrmason.entity.AdminSpQualification;

import lombok.Data;

@Data
public class ResponseAdminSpQualiDto {

	private String message;
	private boolean status;
	private AdminSpQualification addQuilification;
}
