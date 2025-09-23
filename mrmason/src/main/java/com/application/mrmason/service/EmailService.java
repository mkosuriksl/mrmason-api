package com.application.mrmason.service;

import com.application.mrmason.enums.RegSource;

public interface EmailService {
	
	public void sendEmail(String toMail, String body, RegSource regSource);
	public void sendEmail(String toMail, String body);
	
	public void sendWebMail(String toMail, String body);
	public void sendEmail(String toMail, RegSource regSource);
	public void sendEmailPromotion(String toMail, String subject, String body, RegSource regSource);
}
