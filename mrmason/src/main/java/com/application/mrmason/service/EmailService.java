package com.application.mrmason.service;

public interface EmailService {
	
	public void sendMail(String toMail, String body);
	
	public void sendWebMail(String toMail, String body);
}
