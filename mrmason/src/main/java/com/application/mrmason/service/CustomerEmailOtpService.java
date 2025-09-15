package com.application.mrmason.service;

import com.application.mrmason.entity.CustomerEmailOtp;
import com.application.mrmason.enums.RegSource;

public interface CustomerEmailOtpService {

	String isEmailExistsAndRegSource(String email,RegSource regSource);
	public CustomerEmailOtp updateData(String otp,String email,RegSource regSource);	
}
