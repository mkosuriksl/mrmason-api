package com.application.mrmason.service;

import com.application.mrmason.enums.RegSource;

public interface OtpGenerationService {
	
	String generateOtp(String mail,RegSource regSource);
	String generateOtp(String mail);
	String generateEmailOtpByCustomer(String mail);
	public boolean verifyEmailOtpWithCustomer(String email, String enteredOtp);
	boolean verifyOtp(String email, String enteredOtp,RegSource regSource);
	boolean verifyOtp(String email, String enteredOtp);
	String generateMobileOtp(String mobile,RegSource regSource);
	boolean verifyMobileOtp(String mobile, String enteredOtp);
	public String generateMsOtp(String mail, RegSource regSource);
	public String generateMsMobileOtp(String mobile, RegSource regSource) ;
	public String generateCustomerMobileOtp(String mail);
//	public String generateMsMobileOtp(String mobile);
}
