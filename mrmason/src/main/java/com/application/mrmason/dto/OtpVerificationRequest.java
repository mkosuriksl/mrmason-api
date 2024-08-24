package com.application.mrmason.dto;

import com.application.mrmason.enums.RegSource;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OtpVerificationRequest {

	private String contactDetail;

	private RegSource regSource;
	
	private String otp;
}
