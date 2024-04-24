package com.application.mrmason.dto;

import lombok.Data;

@Data
public class ResponseSpLoginDto {
	private String message;
	private String jwtToken;
	private Userdto loginDetails;
}
