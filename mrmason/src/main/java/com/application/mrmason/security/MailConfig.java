package com.application.mrmason.security;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.application.mrmason.entity.AdminMail;
import com.application.mrmason.repository.AdminMailRepo;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Configuration
public class MailConfig {

	@Autowired
	private AdminMailRepo mailRepo;
	
	@Bean
	public JavaMailSender getJavaMailSender() {
		AdminMail smtpConfig = mailRepo.findByEmailid("no_reply@kosuriers.com");

		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost(smtpConfig.getMailHost());
		mailSender.setPort(Integer.parseInt(smtpConfig.getSmtpPort()));
		mailSender.setUsername(smtpConfig.getEmailid());
		mailSender.setPassword(smtpConfig.getPwd());

		Properties props = mailSender.getJavaMailProperties();
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.auth", smtpConfig.getSmtpAuth());
		props.put("spring.mail.properties.mail.smtp.ssl.enable", true);
		//props.put("mail.debug", "true");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.starttls.enable", smtpConfig.getStarttlsEnable());
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = null;
		try {
			helper = new MimeMessageHelper(message, true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			helper.setFrom(smtpConfig.getEmailid());
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mailSender;
	}
}