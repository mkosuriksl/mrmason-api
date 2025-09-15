package com.application.mrmason.service;

import com.application.mrmason.enums.RegSource;

public interface OtpGenerationService {
	
	String generateOtp(String mail,RegSource regSource);
	String generateOtp(String mail);
	String generateCustomerOtp(String mail,RegSource regSource);
//	String generateEmailOtpByCustomer(String mail);
	public String generateEmailOtpByCustomerAndRegSource(String mail,RegSource regSource) ;
	public boolean verifyEmailOtpAndRegSourceWithCustomer(String email,RegSource regSource, String enteredOtp);
	boolean verifyOtp(String email, String enteredOtp,RegSource regSource);
	boolean verifyOtp(String email, String enteredOtp);
	String generateMobileOtp(String mobile,RegSource regSource);
	boolean verifyMobileOtp(String mobile, String enteredOtp);
	boolean verifyMobileOtpRegSource(String mobile, String enteredOtp,RegSource regSource);
	public String generateMsOtp(String mail, RegSource regSource);
	public String generateMsMobileOtp(String mobile, RegSource regSource) ;
	public String generateCustomerMobileOtpAndRegSource(String mail,RegSource regSource);
	public String generateCustomerMobileOtp(String mobile, RegSource regSource) ;
	public boolean verifyCustomerEmailOtp(String email, String enteredOtp,RegSource regSource);
//	public String generateMsMobileOtp(String mobile);
}
