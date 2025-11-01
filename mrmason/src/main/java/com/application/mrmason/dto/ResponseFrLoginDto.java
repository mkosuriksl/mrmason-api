package com.application.mrmason.dto;

import lombok.Data;

@Data
public class ResponseFrLoginDto {
	private String message;
	private boolean status;
	private String jwtToken;
	private UserFrDto loginDetails;
}
