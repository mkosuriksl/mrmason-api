package com.application.mrmason.service;

import com.application.mrmason.enums.RegSource;

public interface SmsSender {

	boolean sendSMSMessage(String phoneNumber, String message, RegSource regSource);

	boolean registrationSendSMSMessage(String phoneNumber, String message, RegSource regSource);
	
	public boolean sendSMSMessage(String phoneNumber, RegSource regSource) ;
	
	public boolean sendSMSMessage(String phoneNumber, String otp);
	
	public boolean sendSMSPromotion(String userMobile, String message, RegSource regSource);

}
