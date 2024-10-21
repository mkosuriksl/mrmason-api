package com.application.mrmason.service;

import com.application.mrmason.enums.RegSource;

public interface EmailService {
	
	public void sendEmail(String toMail, String body, RegSource regSource);
	
	public void sendWebMail(String toMail, String body);
}
