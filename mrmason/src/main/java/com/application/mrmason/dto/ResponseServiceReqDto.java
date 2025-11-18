package com.application.mrmason.dto;

import lombok.Data;

@Data
public class ResponseServiceReqDto {
	private String message;
	private boolean status;
	private Object serviceData;
}
