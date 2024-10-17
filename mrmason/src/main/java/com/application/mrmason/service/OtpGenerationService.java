package com.application.mrmason.service;

import com.application.mrmason.enums.RegSource;

public interface OtpGenerationService {
	
	String generateOtp(String mail);
	boolean verifyOtp(String email, String enteredOtp);
	String generateMobileOtp(String mobile,RegSource regSource);
	boolean verifyMobileOtp(String mobile, String enteredOtp);
}
