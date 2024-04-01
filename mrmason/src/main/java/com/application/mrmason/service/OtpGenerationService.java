package com.application.mrmason.service;

public interface OtpGenerationService {
	
	String generateOtp(String mail);
	boolean verifyOtp(String email, String enteredOtp);
}
