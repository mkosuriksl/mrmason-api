package com.application.mrmason.service;

import com.application.mrmason.entity.CustomerMobileOtp;
import com.application.mrmason.enums.RegSource;

public interface CustomerMobileOtpService {
	public CustomerMobileOtp updateData(String otp, String mobile,RegSource regSource);
	public String  isMobileNumExistsAndRegSource(String mobile,RegSource regSource);
}
