package com.applicaion.mrmason.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl {

	@Autowired
	private JavaMailSender mailsender;

	public void sendMail(String toMail, String body) {

		SimpleMailMessage mail = new SimpleMailMessage();

		mail.setTo(toMail);
		mail.setSubject("YOUR OTP FOR LOGIN");
		mail.setText(body);
		mailsender.send(mail);
	}

	public void sendWebMail(String toMail, String body) {

		MimeMessage message = mailsender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");

		try {
			helper.setTo(toMail);
			helper.setSubject("OTP LOGIN SUCCESSFUL");
			helper.setText(body, true);
			mailsender.send(message);
		} catch (MessagingException e) {
			// Handle exception
		}
	}
}
