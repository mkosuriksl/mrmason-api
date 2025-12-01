package com.application.mrmason.dto;

import lombok.Data;

@Data
public class ResponseAdminBhatServiceDto {
	private String message;
	private boolean status;
	private AdminBhatServiceNameDto data;
}

