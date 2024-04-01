package com.application.mrmason.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginDto {
	private String userEmail;
	private String userMobile;
	private String userPassword;
	private String otp;
}
